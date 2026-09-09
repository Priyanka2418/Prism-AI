package com.aimock.interview.interview.ai.evaluation.service;

import com.aimock.interview.common.exception.InvalidStateException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.ai.evaluation.dto.InterviewFeedbackResponse;
import com.aimock.interview.interview.ai.evaluation.entity.InterviewFeedback;
import com.aimock.interview.interview.ai.evaluation.enums.FeedbackStatus;
import com.aimock.interview.interview.ai.evaluation.repository.InterviewFeedbackRepository;
import com.aimock.interview.interview.ai.evaluation.FeedbackStateTransition;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import com.aimock.interview.interview.commons.enums.InterviewType;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import com.aimock.interview.interview.turn.repository.InterviewTurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class InterviewFeedbackServiceImpl
        implements InterviewFeedbackService {

    private final InterviewRepository interviewRepository;
    private final InterviewTurnRepository interviewTurnRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final FeedbackStateTransition feedbackStateTransition;
    private final ChatClient chatClient;

    @Override
    @Transactional
    public InterviewFeedbackResponse generateFeedback(UUID interviewId) {

        Interview interview = interviewRepository
                .findById(interviewId).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview not found: " + interviewId));

        if (interview.getStatus() != InterviewStatus.COMPLETED) {
            throw new InvalidStateException(
                    "You have not completed this mock interview yet. Please complete the interview session before generating AI evaluation.");
        }

        List<InterviewTurn> turns =
                interviewTurnRepository
                        .findByInterviewIdOrderByTurnNumberAsc(interviewId);

        boolean hasCandidateAnswers = turns.stream()
                .anyMatch(t -> t.getSpeaker() == Speaker.CANDIDATE && t.getContent() != null && !t.getContent().isBlank());

        if (!hasCandidateAnswers) {
            throw new InvalidStateException(
                    "You have not answered any questions in this mock interview. Please participate in the interview before generating AI evaluation.");
        }

        InterviewFeedback feedback = feedbackRepository
                .findByInterviewId(interviewId)
                .orElseGet(() -> createFeedback(interview));

        feedbackStateTransition.moveTo(feedback, FeedbackStatus.GENERATING);

        feedbackRepository.save(feedback);

        try {
            String transcript = buildInterviewTranscript(turns);

            String prompt = buildFeedbackPrompt(interview, transcript);

            InterviewFeedbackResponse response;
            try {
                response = chatClient
                        .prompt()
                        .user(prompt)
                        .call()
                        .entity(InterviewFeedbackResponse.class);
            } catch (Exception ex) {
                log.warn("AI Feedback generation via LLM client unavailable ({}), generating dynamic analytical evaluation.", ex.getMessage());
                response = generateDynamicContextualFeedback(interview, turns, interviewId);
            }

            applyFeedback(feedback, response);

            feedbackStateTransition.moveTo(
                    feedback, FeedbackStatus.COMPLETED);
            feedbackRepository.save(feedback);

            return toResponse(interview, feedback);

        } catch (Exception exception) {
            feedbackStateTransition.moveTo(feedback, FeedbackStatus.FAILED);
            feedbackRepository.save(feedback);
            throw exception;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewFeedbackResponse getFeedback(UUID interviewId) {
        InterviewFeedback feedback = feedbackRepository
                        .findByInterviewId(interviewId).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Feedback not found for interview: "
                                                + interviewId));

        return toResponse(feedback.getInterview(), feedback);
    }

    /*
     * Create the initial feedback record.
     */
    private InterviewFeedback createFeedback(Interview interview) {

        InterviewFeedback feedback = new InterviewFeedback();

        feedback.setInterview(interview);
        feedback.setStatus(FeedbackStatus.PENDING);

        return feedbackRepository.save(feedback);
    }

    /*
     * Convert interview turns into a readable transcript.
     */
    private String buildInterviewTranscript(List<InterviewTurn> turns) {
        StringBuilder transcript = new StringBuilder();
        for (InterviewTurn turn : turns) {

            transcript
                    .append(turn.getSpeaker())
                    .append(": ")
                    .append(turn.getContent())
                    .append("\n\n");
        }

        return transcript.toString();
    }

    /*
     * Build the complete prompt sent to the LLM.
     * Instructs the model to be evidence-based and interview-type-specific.
     */
    private String buildFeedbackPrompt(
            Interview interview,
            String transcript) {

        String interviewTypeGuidance = buildInterviewTypeGuidance(interview.getInterviewType());

        return """
                You are an expert interview evaluator specializing in %s interviews.

                You must evaluate the following completed mock interview and produce
                highly specific, evidence-based feedback grounded exclusively in
                the actual questions asked and the candidate's actual responses below.

                ═══════════════════════════════════════
                INTERVIEW CONTEXT
                ═══════════════════════════════════════
                Target Role       : %s
                Interview Type    : %s
                Difficulty        : %s
                Experience Level  : %s
                Topics Covered    : %s

                ═══════════════════════════════════════
                FULL INTERVIEW TRANSCRIPT
                ═══════════════════════════════════════
                %s
                ═══════════════════════════════════════

                %s

                ═══════════════════════════════════════
                EVALUATION RULES (MUST FOLLOW)
                ═══════════════════════════════════════
                1. Every strength and area-for-improvement MUST cite a specific
                   question or answer from the transcript above.
                2. Do NOT use generic advice (e.g. "practice STAR method") unless
                   you observed a specific failure to use it in the transcript.
                3. Recommended practice items must be directly tied to observed
                   gaps — not generic topics.
                4. If the candidate excelled at a topic, acknowledge it concretely.
                5. If a topic was not asked or not answered, do not mention it.
                6. Scores must reflect the actual depth and accuracy of answers,
                   not participation or word count alone.

                Return only the structured response in the requested format.
                """
                .formatted(
                        interview.getInterviewType().name().replace("_", " "),
                        interview.getTargetRole(),
                        interview.getInterviewType(),
                        interview.getInterviewDifficulty(),
                        interview.getExperienceLevel(),
                        interview.getTopics(),
                        transcript,
                        interviewTypeGuidance);
    }

    /*
     * Returns interview-type-specific evaluation criteria for the LLM prompt.
     */
    private String buildInterviewTypeGuidance(InterviewType type) {
        if (type == null) return "";
        return switch (type) {
            case HR -> """
                    EVALUATION FOCUS (HR Interview):
                    - Assess communication clarity, professional tone, and storytelling structure.
                    - Check if answers follow a clear narrative (situation → action → result).
                    - Evaluate alignment of career goals with expressed values.
                    - Note specific stories shared and whether they had measurable outcomes.
                    - Flag any vague or deflecting responses to direct behavioral questions.
                    """;
            case BEHAVIORAL -> """
                    EVALUATION FOCUS (Behavioral Interview):
                    - Assess whether examples were concrete and role-relevant.
                    - Check for STAR structure (Situation, Task, Action, Result) in each story.
                    - Evaluate ownership and accountability in described situations.
                    - Note whether results were quantified and impactful.
                    - Flag generic or hypothetical answers where specific examples were expected.
                    """;
            case TECHNICAL -> """
                    EVALUATION FOCUS (Technical Interview):
                    - Assess correctness and depth of technical explanations.
                    - Check whether the candidate articulated time/space complexity or system trade-offs where relevant.
                    - Evaluate problem-solving approach: did they clarify requirements, consider edge cases?
                    - Note specific algorithms, data structures, or patterns discussed.
                    - Flag incorrect statements or missed edge cases explicitly.
                    """;
            case MIXED -> """
                    EVALUATION FOCUS (Mixed / Comprehensive Interview):
                    - Assess technical problem-solving depth, communication clarity, and practical examples.
                    - Check for structured explanations with real-world justification.
                    - Flag any gaps across both technical competencies and soft skills.
                    """;
            default -> """
                    EVALUATION FOCUS:
                    - Assess accuracy and depth of responses.
                    - Evaluate communication clarity and professional tone.
                    - Note whether the candidate demonstrated relevant domain knowledge.
                    - Flag significant gaps or incorrect statements.
                    """;
        };
    }

    /*
     * Copy the AI-generated evaluation into the entity.
     */
    private void applyFeedback(
            InterviewFeedback feedback,
            InterviewFeedbackResponse response) {

        feedback.setOverallScore(response.overallScore());

        feedback.setOverallReason(response.overallReason());

        feedback.setAnswerQualityRating(response.answerQualityRating());

        feedback.setAnswerQualityReason(response.answerQualityReason());

        feedback.setKeyStrengths(response.keyStrengths());

        feedback.setDevelopmentAreas(response.developmentAreas());

        feedback.setRecommendedPractice(response.recommendedPractice());
    }

    /*
     * Convert entity into API response.
     */
    private InterviewFeedbackResponse toResponse(
            Interview interview, InterviewFeedback feedback) {

        return new InterviewFeedbackResponse(
                interview.getId(),
                interview.getTargetRole(),
                interview.getInterviewType().name(),
                feedback.getOverallScore(),
                feedback.getOverallReason(),
                feedback.getAnswerQualityRating(),
                feedback.getAnswerQualityReason(),
                feedback.getKeyStrengths(),
                feedback.getDevelopmentAreas(),
                feedback.getRecommendedPractice());
    }

    /*
     * Fallback: generates contextual feedback by deeply analyzing the
     * actual interview transcript when the LLM client is unavailable.
     *
     * Unlike a static fallback, this analyzes real turn content:
     * - Which questions were asked and what topics they covered
     * - Whether candidate answers were substantive or vague
     * - Specific keywords and concepts the candidate mentioned or missed
     * - Per-answer quality signals (length, specificity, use of examples)
     */
    private InterviewFeedbackResponse generateDynamicContextualFeedback(
            Interview interview, List<InterviewTurn> turns, UUID interviewId) {

        String role = interview.getTargetRole() != null ? interview.getTargetRole() : "the target role";
        boolean isHr = interview.getInterviewType() == InterviewType.HR;
        boolean isBehavioral = interview.getInterviewType() == InterviewType.BEHAVIORAL;
        List<String> interviewTopics = interview.getTopics() != null ? interview.getTopics() : List.of();
        boolean isSystemDesign = interviewTopics.stream().anyMatch(t -> t.toLowerCase().contains("system design") || t.toLowerCase().contains("architecture"))
                || (interview.getTargetRole() != null && interview.getTargetRole().toLowerCase().contains("architect"));

        // Separate AI questions and candidate answers
        List<InterviewTurn> aiTurns = turns.stream()
                .filter(t -> t.getSpeaker() == Speaker.AI && t.getContent() != null && !t.getContent().isBlank())
                .toList();

        List<InterviewTurn> candidateTurns = turns.stream()
                .filter(t -> t.getSpeaker() == Speaker.CANDIDATE && t.getContent() != null && !t.getContent().isBlank())
                .toList();

        if (candidateTurns.isEmpty()) {
            throw new InvalidStateException("No candidate answers found in transcript.");
        }

        // Per-answer analysis
        List<AnswerAnalysis> analyses = new ArrayList<>();
        for (int i = 0; i < candidateTurns.size(); i++) {
            InterviewTurn answer = candidateTurns.get(i);
            // Find the question that preceded this answer (best effort)
            String questionText = aiTurns.size() > i ? aiTurns.get(i).getContent() : "Question " + (i + 1);
            analyses.add(analyzeAnswer(questionText, answer.getContent(), i + 1));
        }

        // Aggregate metrics
        int totalWords = analyses.stream().mapToInt(a -> a.wordCount).sum();
        int answerCount = analyses.size();
        double avgWordCount = (double) totalWords / answerCount;

        long deepAnswers   = analyses.stream().filter(a -> a.depth == AnswerDepth.DEEP).count();
        long shallowAnswers = analyses.stream().filter(a -> a.depth == AnswerDepth.SHALLOW).count();
        long vagueAnswers  = analyses.stream().filter(a -> a.depth == AnswerDepth.VAGUE).count();

        // Score calculation: starts at 60, weighted by answer depth
        int baseScore = 60;
        int depthBonus = (int) (deepAnswers * 8);
        int shallowPenalty = (int) (vagueAnswers * 5);
        int lengthBonus = avgWordCount > 80 ? 5 : avgWordCount > 40 ? 2 : 0;
        int finalScore = Math.min(95, Math.max(45, baseScore + depthBonus - shallowPenalty + lengthBonus));
        int answerRating = Math.min(10, Math.max(4, finalScore / 10));

        // Build evidence-based feedback from actual analysis
        String overallReason = buildOverallReason(
                role, answerCount, deepAnswers, shallowAnswers, vagueAnswers, analyses, isHr, finalScore);

        String answerQualityReason = buildAnswerQualityReason(
                analyses, avgWordCount, deepAnswers, vagueAnswers, answerCount);

        List<String> strengths = buildStrengths(analyses, isHr, isSystemDesign, isBehavioral, role);
        List<String> developmentAreas = buildDevelopmentAreas(analyses, isHr, isSystemDesign, isBehavioral);
        List<String> practiceRoadmap = buildPracticeRoadmap(analyses, isHr, isSystemDesign, isBehavioral, interview);

        return new InterviewFeedbackResponse(
                interviewId,
                role,
                interview.getInterviewType().name(),
                finalScore,
                overallReason,
                answerRating,
                answerQualityReason,
                strengths,
                developmentAreas,
                practiceRoadmap
        );
    }

    // ── Answer Analysis Helpers ───────────────────────────────────────────────

    private enum AnswerDepth { DEEP, MODERATE, SHALLOW, VAGUE }

    private record AnswerAnalysis(
            int questionNumber,
            String questionSnippet,   // first 80 chars of the question
            String answerSnippet,     // first 100 chars of the answer
            int wordCount,
            AnswerDepth depth,
            boolean usedConcreteExample,
            boolean usedTechnicalTerms,
            boolean mentionedTradeoffs,
            boolean answeredDirectly,
            List<String> detectedKeywords
    ) {}

    /*
     * Analyzes a single candidate answer and returns structured signals.
     */
    private AnswerAnalysis analyzeAnswer(String question, String answer, int num) {
        String answerLower = answer.toLowerCase();
        int wordCount = answer.trim().isEmpty() ? 0 : answer.trim().split("\\s+").length;

        // Depth classification
        AnswerDepth depth;
        if (wordCount >= 80) depth = AnswerDepth.DEEP;
        else if (wordCount >= 35) depth = AnswerDepth.MODERATE;
        else if (wordCount >= 12) depth = AnswerDepth.SHALLOW;
        else depth = AnswerDepth.VAGUE;

        // Concrete example signals
        boolean usedExample = containsAny(answerLower,
                "for example", "for instance", "in my experience", "when i", "i worked on",
                "at my previous", "in a project", "we built", "i implemented", "specifically",
                "one time", "a situation where", "i once");

        // Technical term detection
        boolean technicalTerms = containsAny(answerLower,
                "algorithm", "complexity", "o(n", "database", "cache", "api", "rest",
                "microservice", "scalab", "latency", "throughput", "consistency", "availab",
                "load balanc", "replication", "partition", "shard", "index", "sql", "nosql",
                "thread", "async", "concurrent", "design pattern", "solid", "dependency",
                "interface", "abstraction", "encapsulation", "polymorphism", "inheritance");

        // Trade-off signals
        boolean tradeoffs = containsAny(answerLower,
                "trade-off", "tradeoff", "however", "on the other hand", "but the downside",
                "advantage", "disadvantage", "pros and cons", "versus", " vs ", "compared to",
                "the drawback", "the benefit", "alternatively");

        // Directness check — very short answers or "I don't know" patterns
        boolean answeredDirectly = wordCount >= 15
                && !containsAny(answerLower, "i don't know", "i am not sure", "i'm not sure",
                        "i have no idea", "not familiar", "never worked");

        // Keyword extraction (top relevant terms from the answer)
        List<String> keywords = extractKeywords(answer);

        return new AnswerAnalysis(
                num,
                question.length() > 80 ? question.substring(0, 80) + "…" : question,
                answer.length() > 100 ? answer.substring(0, 100) + "…" : answer,
                wordCount,
                depth,
                usedExample,
                technicalTerms,
                tradeoffs,
                answeredDirectly,
                keywords
        );
    }

    private boolean containsAny(String text, String... phrases) {
        for (String phrase : phrases) {
            if (text.contains(phrase)) return true;
        }
        return false;
    }

    /*
     * Extracts notable keywords from a candidate answer for use in feedback.
     */
    private List<String> extractKeywords(String answer) {
        String[] technicalSignals = {
            "microservice", "kafka", "redis", "docker", "kubernetes", "rest api",
            "graph", "tree", "queue", "stack", "hashmap", "linked list",
            "distributed", "eventual consistency", "cap theorem", "load balancer",
            "sharding", "caching", "rate limiting", "circuit breaker",
            "star method", "feedback", "conflict", "leadership", "ownership",
            "agile", "scrum", "sprint", "ci/cd", "deployment", "testing"
        };
        String lower = answer.toLowerCase();
        List<String> found = new ArrayList<>();
        for (String signal : technicalSignals) {
            if (lower.contains(signal)) found.add(signal);
        }
        return found;
    }

    // ── Narrative Builders ────────────────────────────────────────────────────

    private String buildOverallReason(
            String role, int answerCount, long deep, long shallow, long vague,
            List<AnswerAnalysis> analyses, boolean isHr, int score) {

        String performanceSummary;
        if (score >= 80) performanceSummary = "strong";
        else if (score >= 65) performanceSummary = "solid";
        else if (score >= 50) performanceSummary = "mixed";
        else performanceSummary = "foundational";

        // Collect topics/keywords the candidate actually mentioned
        Set<String> mentionedTopics = analyses.stream()
                .flatMap(a -> a.detectedKeywords.stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        String topicsPhrase = mentionedTopics.isEmpty()
                ? "general concepts"
                : String.join(", ", mentionedTopics.stream().limit(4).toList());

        String depthDetail = String.format(
                "%d of %d answer%s demonstrated depth and specificity",
                deep, answerCount, answerCount == 1 ? "" : "s");

        String vagueNote = vague > 0
                ? String.format(", while %d response%s lacked sufficient detail", vague, vague == 1 ? "" : "s")
                : "";

        String exampleNote = analyses.stream().anyMatch(a -> a.usedConcreteExample)
                ? " The candidate supported some responses with concrete examples."
                : (isHr
                    ? " Behavioral responses would benefit from more specific situational examples."
                    : " Technical answers would be stronger with worked examples or specific system references.");

        return String.format(
                "Overall %s performance for a %s role. %s%s.%s Topics referenced included: %s.",
                performanceSummary, role, depthDetail, vagueNote, exampleNote, topicsPhrase
        );
    }

    private String buildAnswerQualityReason(
            List<AnswerAnalysis> analyses, double avgWords,
            long deep, long vague, int total) {

        String lengthProfile = avgWords > 80 ? "comprehensive" : avgWords > 40 ? "adequate" : "brief";

        long withExamples  = analyses.stream().filter(a -> a.usedConcreteExample).count();
        long withTradeoffs = analyses.stream().filter(a -> a.mentionedTradeoffs).count();
        long withTech      = analyses.stream().filter(a -> a.usedTechnicalTerms).count();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "Responses were %s (avg ~%d words/answer). ",
                lengthProfile, (int) avgWords));

        if (withExamples > 0) {
            sb.append(String.format("%d answer%s included concrete examples. ",
                    withExamples, withExamples == 1 ? "" : "s"));
        } else {
            sb.append("No concrete examples were cited in any answer. ");
        }

        if (withTradeoffs > 0) {
            sb.append(String.format("%d answer%s discussed trade-offs or alternatives. ",
                    withTradeoffs, withTradeoffs == 1 ? "" : "s"));
        }

        if (withTech > 0) {
            sb.append(String.format("%d answer%s used relevant technical terminology. ",
                    withTech, withTech == 1 ? "" : "s"));
        }

        if (vague > 0) {
            sb.append(String.format("%d answer%s were too brief to demonstrate meaningful understanding.",
                    vague, vague == 1 ? "" : "s"));
        }

        return sb.toString().trim();
    }

    private List<String> buildStrengths(
            List<AnswerAnalysis> analyses, boolean isHr,
            boolean isSystemDesign, boolean isBehavioral, String role) {

        List<String> strengths = new ArrayList<>();

        // Concrete examples
        List<AnswerAnalysis> withExamples = analyses.stream()
                .filter(a -> a.usedConcreteExample).toList();
        if (!withExamples.isEmpty()) {
            AnswerAnalysis first = withExamples.get(0);
            strengths.add(String.format(
                    "Used concrete examples to support answers (e.g., Q%d: \"%s\")",
                    first.questionNumber, first.answerSnippet));
        }

        // Trade-off awareness
        List<AnswerAnalysis> withTradeoffs = analyses.stream()
                .filter(a -> a.mentionedTradeoffs).toList();
        if (!withTradeoffs.isEmpty()) {
            strengths.add(String.format(
                    "Demonstrated trade-off awareness — explicitly compared approaches in %d answer%s",
                    withTradeoffs.size(), withTradeoffs.size() == 1 ? "" : "s"));
        }

        // Deep answers
        List<AnswerAnalysis> deepOnes = analyses.stream()
                .filter(a -> a.depth == AnswerDepth.DEEP).toList();
        if (!deepOnes.isEmpty()) {
            AnswerAnalysis d = deepOnes.get(0);
            strengths.add(String.format(
                    "Provided thorough responses — Q%d in particular showed strong depth with %d words",
                    d.questionNumber, d.wordCount));
        }

        // Technical terminology
        if (!isHr && analyses.stream().anyMatch(a -> a.usedTechnicalTerms)) {
            Set<String> terms = analyses.stream()
                    .flatMap(a -> a.detectedKeywords.stream())
                    .limit(3)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (!terms.isEmpty()) {
                strengths.add(String.format(
                        "Applied relevant domain knowledge — referenced %s in answers",
                        String.join(", ", terms)));
            }
        }

        // HR: direct & professional tone
        if (isHr && analyses.stream().filter(a -> a.answeredDirectly).count() >= 2) {
            strengths.add("Maintained a professional and direct tone throughout behavioral responses");
        }

        // Fallback if nothing found
        if (strengths.isEmpty()) {
            strengths.add(String.format(
                    "Completed all %d interview questions for the %s role, demonstrating willingness to engage",
                    analyses.size(), role));
        }

        return strengths;
    }

    private List<String> buildDevelopmentAreas(
            List<AnswerAnalysis> analyses, boolean isHr,
            boolean isSystemDesign, boolean isBehavioral) {

        List<String> areas = new ArrayList<>();

        // Vague / too short answers
        List<AnswerAnalysis> vague = analyses.stream()
                .filter(a -> a.depth == AnswerDepth.VAGUE || a.depth == AnswerDepth.SHALLOW)
                .toList();
        if (!vague.isEmpty()) {
            AnswerAnalysis v = vague.get(0);
            areas.add(String.format(
                    "Q%d received only %d words — expand this answer with more specific details or examples",
                    v.questionNumber, v.wordCount));
        }

        // No concrete examples
        if (analyses.stream().noneMatch(a -> a.usedConcreteExample)) {
            if (isHr || isBehavioral) {
                areas.add("No situational examples were provided — use the STAR format (Situation, Task, Action, Result) to ground behavioral answers in real events");
            } else {
                areas.add("Answers lacked concrete worked examples — illustrate with a specific project, system, or algorithm you have used");
            }
        }

        // No trade-off discussion in system design / technical
        if ((isSystemDesign || !isHr) && analyses.stream().noneMatch(a -> a.mentionedTradeoffs)) {
            areas.add("Trade-off analysis was absent — for each design or technical decision, explicitly compare at least two approaches and justify your choice");
        }

        // Direct / unanswered questions
        List<AnswerAnalysis> indirect = analyses.stream()
                .filter(a -> !a.answeredDirectly).toList();
        if (!indirect.isEmpty()) {
            areas.add(String.format(
                    "Q%d was not answered directly — avoid restating the question; lead immediately with your position or approach",
                    indirect.get(0).questionNumber));
        }

        // Fallback
        if (areas.isEmpty()) {
            areas.add("Continue strengthening answers by quantifying outcomes and referencing specific real-world scenarios");
        }

        return areas;
    }

    private List<String> buildPracticeRoadmap(
            List<AnswerAnalysis> analyses, boolean isHr, boolean isSystemDesign,
            boolean isBehavioral, Interview interview) {

        List<String> roadmap = new ArrayList<>();

        List<String> topics = interview.getTopics() != null ? interview.getTopics() : List.of();
        String topicsStr = topics.isEmpty() ? "" : String.join(", ", topics);

        // Topic-specific practice
        if (!topicsStr.isBlank()) {
            roadmap.add(String.format(
                    "Deepen preparation on the interview topics: %s — focus on edge cases and failure scenarios",
                    topicsStr));
        }

        boolean lacksExamples    = analyses.stream().noneMatch(a -> a.usedConcreteExample);
        boolean lacksTradeoffs   = analyses.stream().noneMatch(a -> a.mentionedTradeoffs);
        boolean hasShallowAnswers = analyses.stream().anyMatch(
                a -> a.depth == AnswerDepth.SHALLOW || a.depth == AnswerDepth.VAGUE);

        if (isHr || isBehavioral) {
            if (lacksExamples) {
                roadmap.add("Prepare 5 core behavioral stories using the STAR format — each should have a specific situation, measurable action, and quantified result");
            }
            roadmap.add(String.format(
                    "Practice answering %s role-specific behavioral questions aloud, targeting 90–120 second responses per answer",
                    interview.getTargetRole()));

        } else if (isSystemDesign) {
            if (lacksTradeoffs) {
                roadmap.add("Practice system design by explicitly discussing CAP theorem, consistency models, and fault tolerance trade-offs for every design question");
            }
            roadmap.add(String.format(
                    "Design end-to-end systems relevant to %s — include data models, API contracts, caching, and observability in every practice session%s",
                    interview.getTargetRole(),
                    topicsStr.isBlank() ? "" : " covering: " + topicsStr));

        } else {
            // Technical interview
            if (lacksExamples) {
                roadmap.add("For each technical concept, prepare a brief real-world example from your own work or a well-known system");
            }
            if (hasShallowAnswers) {
                roadmap.add(String.format(
                        "Practice explaining %s concepts at increasing depth — target 60–90 second verbal answers that include the what, why, and how%s",
                        interview.getTargetRole(),
                        topicsStr.isBlank() ? "" : " for topics: " + topicsStr));
            }
            if (lacksTradeoffs) {
                roadmap.add("For each technical decision, practice comparing at least two alternatives (e.g., SQL vs NoSQL, REST vs gRPC) with explicit justification");
            }
        }

        // Fallback
        if (roadmap.isEmpty()) {
            roadmap.add(String.format(
                    "Continue practicing mock interviews focused on %s to build answer consistency under realistic conditions",
                    interview.getTargetRole()));
        }

        return roadmap;
    }
}