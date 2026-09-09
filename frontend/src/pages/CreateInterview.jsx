import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { useAuth } from "../context/AuthContext";
import { createInterview } from "../api/interviewApi";

const POPULAR_TOPICS = [
  "Java",
  "Spring Boot",
  "React",
  "Microservices",
  "System Design",
  "Data Structures & Algorithms",
  "SQL & Database Design",
  "REST APIs",
  "Docker & Kubernetes",
  "Cloud Architecture",
  "Behavioral & Leadership",
  "Object-Oriented Programming",
];

export default function CreateInterview() {
  const { candidateProfile } = useAuth();
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [targetRole, setTargetRole] = useState(
    candidateProfile?.targetRole?.replace(/_/g, " ") || "Software Engineer"
  );
  const [experienceLevel, setExperienceLevel] = useState(
    candidateProfile?.experienceLevel || "MID_LEVEL"
  );
  const [interviewType, setInterviewType] = useState("TECHNICAL");
  const [interviewDifficulty, setInterviewDifficulty] = useState("MEDIUM");
  const [durationMinutes, setDurationMinutes] = useState(30);
  const [topics, setTopics] = useState(["Java", "Spring Boot", "REST APIs"]);
  const [topicInput, setTopicInput] = useState("");
  const [error, setError] = useState("");
  const [isCreating, setIsCreating] = useState(false);

  const handleAddTopic = (e) => {
    if ((e.key === "Enter" || e.type === "click") && topicInput.trim()) {
      e.preventDefault();
      if (!topics.includes(topicInput.trim())) {
        setTopics([...topics, topicInput.trim()]);
      }
      setTopicInput("");
    }
  };

  const togglePopularTopic = (topic) => {
    if (topics.includes(topic)) {
      setTopics(topics.filter((t) => t !== topic));
    } else {
      setTopics([...topics, topic]);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!targetRole.trim()) {
      setError("Please specify a target role.");
      return;
    }
    if (topics.length === 0) {
      setError("Please include at least one topic for the interview.");
      return;
    }

    setError("");
    setIsCreating(true);

    try {
      const response = await createInterview({
        title: title.trim() || `${targetRole.trim()} (${interviewType})`,
        targetRole: targetRole.trim(),
        interviewType,
        interviewDifficulty,
        experienceLevel,
        durationMinutes: Number(durationMinutes),
        topics,
      });

      // Redirect directly to the interview room
      navigate(`/interviews/${response.id}/room`);
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to create mock interview.");
    } finally {
      setIsCreating(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-4xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="mb-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 text-[#2DD4BF] text-xs font-bold uppercase tracking-wider mb-2">
            <i className="fa-solid fa-wand-magic-sparkles" /> AI Mock Simulation
          </div>
          <h1 className="text-3xl font-extrabold tracking-tight text-white">
            Configure Your Mock Interview
          </h1>
          <p className="text-sm text-[#94A3B8] mt-1">
            Customize the persona, scope, and technical depth. Prism.AI generates dynamic, adaptive questions in real-time.
          </p>
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-3">
            <i className="fa-solid fa-circle-exclamation text-lg" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Card 1: Role & Type */}
          <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
            <h2 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
              <i className="fa-solid fa-briefcase text-[#2DD4BF]" />
              Role & Interview Format
            </h2>

            <div className="space-y-6">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Mock Interview Name / Title (Optional)
                </label>
                <input
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Google SDE 1 Preparation, System Design Round 1"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                    Target Role or Position
                  </label>
                  <input
                    type="text"
                    required
                    value={targetRole}
                    onChange={(e) => setTargetRole(e.target.value)}
                    placeholder="e.g. Senior Backend Java Engineer"
                    className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                    Experience Level
                  </label>
                  <select
                    value={experienceLevel}
                    onChange={(e) => setExperienceLevel(e.target.value)}
                    className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF]"
                  >
                    <option value="STUDENT">Student</option>
                    <option value="FRESHER">Fresher (0 - 1 yr)</option>
                    <option value="JUNIOR">Junior (1 - 3 yrs)</option>
                    <option value="MID_LEVEL">Mid-Level (3 - 5 yrs)</option>
                    <option value="SENIOR">Senior (5+ yrs)</option>
                  </select>
                </div>
              </div>

              {/* Interview Type Cards */}
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Interview Type
                </label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                  {[
                    { id: "TECHNICAL", label: "Technical", icon: "fa-code" },
                    { id: "BEHAVIORAL", label: "Behavioral", icon: "fa-users" },
                    { id: "HR", label: "HR / Fit", icon: "fa-handshake" },
                    { id: "MIXED", label: "Mixed / Full", icon: "fa-cubes" },
                  ].map((type) => (
                    <button
                      key={type.id}
                      type="button"
                      onClick={() => setInterviewType(type.id)}
                      className={`p-4 rounded-2xl border text-center transition-all flex flex-col items-center gap-2 ${
                        interviewType === type.id
                          ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF] shadow-[0_0_15px_rgba(45,212,191,0.2)]"
                          : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/30 hover:text-white"
                      }`}
                    >
                      <i className={`fa-solid ${type.icon} text-lg`} />
                      <span className="text-xs font-bold uppercase">{type.label}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Card 2: Difficulty & Duration */}
          <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
            <h2 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
              <i className="fa-solid fa-sliders text-[#2DD4BF]" />
              Difficulty & Time Budget
            </h2>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {/* Difficulty */}
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Starting Difficulty
                </label>
                <div className="grid grid-cols-3 gap-2">
                  {[
                    { id: "EASY", label: "Easy", desc: "Foundational" },
                    { id: "MEDIUM", label: "Medium", desc: "Industry Standard" },
                    { id: "HARD", label: "Hard", desc: "Deep Dive" },
                  ].map((diff) => (
                    <button
                      key={diff.id}
                      type="button"
                      onClick={() => setInterviewDifficulty(diff.id)}
                      className={`p-3 rounded-xl border text-center transition-all ${
                        interviewDifficulty === diff.id
                          ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                          : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/30"
                      }`}
                    >
                      <span className="block text-xs font-bold uppercase">{diff.label}</span>
                      <span className="block text-[10px] text-[#94A3B8] mt-0.5">{diff.desc}</span>
                    </button>
                  ))}
                </div>
                <p className="text-[11px] text-[#94A3B8] mt-2 italic">
                  Note: The AI adaptively adjusts difficulty based on your answers.
                </p>
              </div>

              {/* Duration */}
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Duration (Minutes)
                </label>
                <div className="grid grid-cols-4 gap-2">
                  {[15, 30, 45, 60].map((mins) => (
                    <button
                      key={mins}
                      type="button"
                      onClick={() => setDurationMinutes(mins)}
                      className={`py-3 rounded-xl border text-center font-bold text-sm transition-all ${
                        durationMinutes === mins
                          ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                          : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/30"
                      }`}
                    >
                      {mins}m
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Card 3: Topics */}
          <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
            <h2 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
              <i className="fa-solid fa-tags text-[#2DD4BF]" />
              Target Topics & Technologies
            </h2>
            <p className="text-xs text-[#94A3B8] mb-4">
              Click popular technologies or add your own specific focus areas.
            </p>

            {/* Popular Topics Chips */}
            <div className="flex flex-wrap gap-2 mb-6">
              {POPULAR_TOPICS.map((topic) => {
                const selected = topics.includes(topic);
                return (
                  <button
                    key={topic}
                    type="button"
                    onClick={() => togglePopularTopic(topic)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5 ${
                      selected
                        ? "bg-[#2DD4BF] text-[#0F172A] shadow-[0_0_12px_rgba(45,212,191,0.3)]"
                        : "bg-[#0F172A] text-[#94A3B8] border border-[#334155] hover:text-white hover:border-[#2DD4BF]/40"
                    }`}
                  >
                    <i className={`fa-solid ${selected ? "fa-check" : "fa-plus"} text-[10px]`} />
                    {topic}
                  </button>
                );
              })}
            </div>

            {/* Custom Topic Input */}
            <div className="flex gap-2 mb-4">
              <input
                type="text"
                value={topicInput}
                onChange={(e) => setTopicInput(e.target.value)}
                onKeyDown={handleAddTopic}
                placeholder="Add custom topic (e.g. Kafka, Redis, OAuth 2.0)..."
                className="flex-1 px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
              />
              <button
                type="button"
                onClick={handleAddTopic}
                className="px-5 py-2.5 rounded-xl bg-[#334155] text-white hover:bg-[#2DD4BF] hover:text-[#0F172A] font-medium transition-colors"
              >
                Add Topic
              </button>
            </div>

            {/* Selected Topics Pill Container */}
            <div className="flex flex-wrap gap-2 min-h-[44px] p-2.5 rounded-xl bg-[#0F172A]/70 border border-[#334155]">
              {topics.length === 0 ? (
                <span className="text-xs text-[#94A3B8]/60 p-1">No topics selected yet.</span>
              ) : (
                topics.map((topic) => (
                  <span
                    key={topic}
                    className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg bg-[#2DD4BF]/10 text-[#2DD4BF] border border-[#2DD4BF]/20 text-xs font-semibold"
                  >
                    {topic}
                    <button
                      type="button"
                      onClick={() => togglePopularTopic(topic)}
                      className="hover:text-red-400"
                    >
                      &times;
                    </button>
                  </span>
                ))
              )}
            </div>
          </div>

          {/* Submit */}
          <div className="flex items-center justify-end gap-4">
            <button
              type="button"
              onClick={() => navigate("/candidate/dashboard")}
              className="px-6 py-4 rounded-xl border border-[#334155] text-[#94A3B8] font-bold text-sm hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isCreating}
              className="px-8 py-4 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-sm shadow-[0_0_25px_rgba(45,212,191,0.35)] hover:shadow-[0_0_35px_rgba(45,212,191,0.5)] transition-all active:scale-[0.98] disabled:opacity-60 flex items-center gap-2"
            >
              {isCreating ? (
                <>
                  <i className="fa-solid fa-spinner fa-spin" />
                  Generating Simulation Room...
                </>
              ) : (
                <>
                  <i className="fa-solid fa-play" />
                  <span>Launch Mock Interview</span>
                </>
              )}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}
