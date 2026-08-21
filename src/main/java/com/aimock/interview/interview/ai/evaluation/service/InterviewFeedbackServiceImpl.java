package com.aimock.interview.interview.ai.evaluation.service;

import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.ai.evaluation.dto.InterviewFeedbackResponse;
import com.aimock.interview.interview.ai.evaluation.entity.InterviewFeedback;
import com.aimock.interview.interview.ai.evaluation.enums.FeedbackStatus;
import com.aimock.interview.interview.ai.evaluation.repository.InterviewFeedbackRepository;
import com.aimock.interview.interview.ai.evaluation.FeedbackStateTransition;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import com.aimock.interview.interview.turn.repository.InterviewTurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

        InterviewFeedback feedback = feedbackRepository
                .findByInterviewId(interviewId)
                .orElseGet(() -> createFeedback(interview));

        feedbackStateTransition.moveTo(feedback, FeedbackStatus.GENERATING);

        feedbackRepository.save(feedback);

        try {
            List<InterviewTurn> turns =
                    interviewTurnRepository
                            .findByInterviewIdOrderByTurnNumberAsc(interviewId);

            String transcript = buildInterviewTranscript(turns);

            String prompt = buildFeedbackPrompt(interview, transcript);

            InterviewFeedbackResponse response =
                    chatClient
                            .prompt()
                            .user(prompt)
                            .call()
                            .entity(InterviewFeedbackResponse.class);

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
     */
    private String buildFeedbackPrompt(
            Interview interview,
            String transcript) {

        return """
                You are an expert technical interview evaluator.

                Evaluate the candidate's complete interview.

                Interview details:

                Target role:
                %s

                Interview type:
                %s

                Interview difficulty:
                %s

                Experience level:
                %s

                Topics:
                %s

                Interview transcript:
                ----------------------
                %s
                ----------------------

                Evaluate the candidate based only on
                the information present in the interview.

                Return:

                overallScore:
                Integer from 0 to 100.

                overallReason:
                Explain the overall score using evidence
                from the interview.

                answerQualityRating:
                Integer from 0 to 10.

                answerQualityReason:
                Explain the answer quality using evidence
                from the candidate's responses.

                keyStrengths:
                List of specific strengths demonstrated
                by the candidate.

                developmentAreas:
                List of specific areas that need improvement.

                recommendedPractice:
                List of specific topics or practices
                the candidate should work on.

                Be specific and evidence-based.

                Do not invent information that is not present
                in the transcript.

                Return only the structured response.
                """
                .formatted(
                        interview.getTargetRole(),
                        interview.getInterviewType(),
                        interview.getInterviewDifficulty(),
                        interview.getExperienceLevel(),
                        interview.getTopics(),
                        transcript);
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
}