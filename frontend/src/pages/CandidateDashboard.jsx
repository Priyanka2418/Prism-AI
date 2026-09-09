import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { useAuth } from "../context/AuthContext";
import { getMyInterviews, deleteInterview } from "../api/interviewApi";
import { getCandidateProfile } from "../api/profileApi";
import { getCandidateMentorRequests, getCandidateSessions } from "../api/mentoringApi";
import { deleteInterviewRecording } from "../utils/recordingStorage";

export default function CandidateDashboard() {
  const { user, candidateProfile, setCandidateProfile } = useAuth();
  const navigate = useNavigate();

  const [interviews, setInterviews] = useState([]);
  const [mentorRequests, setMentorRequests] = useState([]);
  const [mentorSessions, setMentorSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        // Ensure profile is loaded, if not redirect to onboarding
        let profile = candidateProfile;
        if (!profile) {
          try {
            profile = await getCandidateProfile();
            setCandidateProfile(profile);
          } catch {
            navigate("/candidate/onboarding", { replace: true });
            return;
          }
        }

        const [interviewsData, requestsData, sessionsData] = await Promise.allSettled([
          getMyInterviews(),
          getCandidateMentorRequests(),
          getCandidateSessions(),
        ]);

        if (interviewsData.status === "fulfilled") {
          setInterviews(interviewsData.value || []);
        }
        if (requestsData.status === "fulfilled") {
          setMentorRequests(requestsData.value || []);
        }
        if (sessionsData.status === "fulfilled") {
          setMentorSessions(sessionsData.value || []);
        }
      } catch (err) {
        console.error("Dashboard load error", err);
      } finally {
        setLoading(false);
      }
    }

    loadData();
  }, [candidateProfile, navigate, setCandidateProfile]);

  const handleDeleteInterview = async (interviewId, e) => {
    e.preventDefault();
    e.stopPropagation();

    if (!window.confirm("Are you sure you want to delete this mock interview session and its recordings?")) {
      return;
    }

    setDeletingId(interviewId);
    try {
      await deleteInterview(interviewId);
      await deleteInterviewRecording(interviewId);
      setInterviews((prev) => prev.filter((i) => i.id !== interviewId));
    } catch (err) {
      console.error(err);
      alert(err.message || "Failed to delete interview.");
    } finally {
      setDeletingId(null);
    }
  };

  const completedCount = interviews.filter((i) => i.status === "COMPLETED").length;
  const inProgressCount = interviews.filter((i) => i.status === "IN_PROGRESS").length;
  const activeSessionsCount = mentorSessions.filter((s) => s.status === "SCHEDULED" || s.status === "ACTIVE").length;

  const statusColor = {
    CREATED: "bg-blue-500/10 text-blue-400 border-blue-500/20",
    IN_PROGRESS: "bg-amber-500/10 text-amber-400 border-amber-500/20",
    COMPLETED: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20",
    CANCELLED: "bg-red-500/10 text-red-400 border-red-500/20",
  };

  const difficultyColor = {
    EASY: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20",
    MEDIUM: "bg-amber-500/10 text-amber-400 border-amber-500/20",
    HARD: "bg-rose-500/10 text-rose-400 border-rose-500/20",
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Welcome Banner */}
        <div className="relative rounded-3xl bg-gradient-to-r from-[#1E293B] via-[#0F172A] to-[#1E293B] border border-[#334155] p-8 mb-8 overflow-hidden shadow-2xl">
          <div className="absolute -right-10 -top-10 w-60 h-60 bg-[#2DD4BF]/10 rounded-full blur-3xl pointer-events-none" />
          <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 text-[#2DD4BF] text-xs font-bold uppercase tracking-wider mb-3">
                <i className="fa-solid fa-sparkles" /> Candidate Hub
              </div>
              <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
                Welcome back, {candidateProfile?.displayName || user?.email?.split("@")[0]}!
              </h1>
              <p className="mt-2 text-[#94A3B8] max-w-2xl text-sm sm:text-base">
                Preparing for <strong className="text-white">{candidateProfile?.targetRole?.replace(/_/g, " ")}</strong> roles. Master your communication, system depth, and technical reasoning with adaptive AI simulations.
              </p>
            </div>

            <Link
              to="/interviews/new"
              className="inline-flex items-center justify-center gap-3 px-6 py-4 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold shadow-[0_0_25px_rgba(45,212,191,0.35)] hover:shadow-[0_0_35px_rgba(45,212,191,0.5)] transition-all active:scale-[0.98] shrink-0"
            >
              <i className="fa-solid fa-play" />
              <span>Start New Interview</span>
            </Link>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
          <div className="p-6 rounded-2xl bg-[#1E293B]/70 border border-[#334155] backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
                Completed Sessions
              </span>
              <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400">
                <i className="fa-solid fa-circle-check" />
              </div>
            </div>
            <div className="mt-4 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-white">{completedCount}</span>
              <span className="text-xs text-[#94A3B8]">interviews evaluated</span>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-[#1E293B]/70 border border-[#334155] backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
                In Progress
              </span>
              <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-amber-400">
                <i className="fa-solid fa-clock-rotate-left" />
              </div>
            </div>
            <div className="mt-4 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-white">{inProgressCount}</span>
              <span className="text-xs text-[#94A3B8]">active sessions</span>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-[#1E293B]/70 border border-[#334155] backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
                Mentorship
              </span>
              <div className="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
                <i className="fa-solid fa-chalkboard-user" />
              </div>
            </div>
            <div className="mt-4 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-white">{mentorRequests.length}</span>
              <span className="text-xs text-[#94A3B8]">
                requests ({activeSessionsCount} active)
              </span>
            </div>
          </div>
        </div>

        {/* Recent Interviews Section */}
        <div className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
              <i className="fa-solid fa-list-check text-[#2DD4BF]" />
              Mock Interview History
            </h2>
            <Link
              to="/interviews/new"
              className="text-xs font-bold text-[#2DD4BF] hover:underline"
            >
              + Create Another
            </Link>
          </div>

          {loading ? (
            <div className="p-12 text-center bg-[#1E293B]/40 rounded-2xl border border-[#334155]">
              <i className="fa-solid fa-spinner fa-spin text-3xl text-[#2DD4BF] mb-3" />
              <p className="text-[#94A3B8]">Loading interview sessions...</p>
            </div>
          ) : interviews.length === 0 ? (
            <div className="p-12 text-center bg-[#1E293B]/40 rounded-2xl border border-dashed border-[#334155]">
              <div className="w-14 h-14 mx-auto rounded-full bg-[#2DD4BF]/10 flex items-center justify-center text-[#2DD4BF] text-2xl mb-4">
                <i className="fa-solid fa-microphone-lines" />
              </div>
              <h3 className="text-lg font-bold text-white mb-1">No Mock Interviews Yet</h3>
              <p className="text-sm text-[#94A3B8] max-w-md mx-auto mb-6">
                Start your very first AI mock interview now! Prism.AI will ask adaptive technical or behavioral questions tailored to your skills.
              </p>
              <Link
                to="/interviews/new"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-sm shadow-[0_0_20px_rgba(45,212,191,0.3)] hover:scale-105 transition-all"
              >
                <i className="fa-solid fa-play" />
                Launch First Mock Interview
              </Link>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {interviews.map((interview) => (
                <div
                  key={interview.id}
                  className="rounded-2xl bg-[#1E293B] border border-[#334155] p-6 hover:border-[#2DD4BF]/40 transition-all flex flex-col justify-between group shadow-lg relative"
                >
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-3">
                      <span
                        className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full border ${
                          statusColor[interview.status] || statusColor.CREATED
                        }`}
                      >
                        {interview.status}
                      </span>
                      <div className="flex items-center gap-2">
                        <span
                          className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full border ${
                            difficultyColor[interview.interviewDifficulty] || difficultyColor.MEDIUM
                          }`}
                        >
                          {interview.interviewDifficulty}
                        </span>

                        {/* Delete button */}
                        <button
                          onClick={(e) => handleDeleteInterview(interview.id, e)}
                          disabled={deletingId === interview.id}
                          title="Delete Mock Interview"
                          className="w-7 h-7 rounded-lg bg-[#0F172A] border border-[#334155] flex items-center justify-center text-[#94A3B8] hover:text-red-400 hover:border-red-500/30 transition-colors text-xs"
                        >
                          {deletingId === interview.id ? (
                            <i className="fa-solid fa-spinner fa-spin text-[10px]" />
                          ) : (
                            <i className="fa-solid fa-trash-can text-[10px]" />
                          )}
                        </button>
                      </div>
                    </div>

                    <h3 className="text-lg font-bold text-white group-hover:text-[#2DD4BF] transition-colors line-clamp-1">
                      {interview.title || interview.targetRole}
                    </h3>
                    <p className="text-xs text-[#94A3B8] mt-1 flex items-center gap-2">
                      <span className="font-semibold text-white/80">{interview.targetRole}</span>
                      <span>•</span>
                      <span className="font-semibold text-indigo-400">{interview.interviewType}</span>
                      <span>•</span>
                      <span>{interview.durationMinutes} mins</span>
                    </p>

                    {/* Topics */}
                    <div className="mt-4 flex flex-wrap gap-1.5">
                      {interview.topics?.slice(0, 3).map((topic, i) => (
                        <span
                          key={i}
                          className="text-[11px] px-2 py-0.5 rounded bg-[#0F172A] text-[#94A3B8] border border-[#334155]/60"
                        >
                          {topic}
                        </span>
                      ))}
                      {interview.topics?.length > 3 && (
                        <span className="text-[11px] px-1.5 py-0.5 rounded bg-[#0F172A] text-[#94A3B8]">
                          +{interview.topics.length - 3}
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Footer Actions */}
                  <div className="mt-6 pt-4 border-t border-[#334155]/60 flex items-center justify-between">
                    <span className="text-[11px] text-[#94A3B8]">
                      {new Date(interview.createdAt).toLocaleDateString(undefined, {
                        month: "short",
                        day: "numeric",
                      })}
                    </span>

                    {interview.status === "COMPLETED" ? (
                      <Link
                        to={`/interviews/${interview.id}/feedback`}
                        className="inline-flex items-center gap-1.5 text-xs font-bold text-[#2DD4BF] hover:underline"
                      >
                        <span>View Feedback</span>
                        <i className="fa-solid fa-arrow-right text-[10px]" />
                      </Link>
                    ) : (
                      <Link
                        to={`/interviews/${interview.id}/room`}
                        className="inline-flex items-center gap-1.5 text-xs font-bold text-[#2DD4BF] hover:underline"
                      >
                        <span>
                          {interview.status === "IN_PROGRESS" ? "Resume Room" : "Enter Room"}
                        </span>
                        <i className="fa-solid fa-arrow-right text-[10px]" />
                      </Link>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Mentorship Section */}
        <div className="mt-12 p-8 rounded-3xl bg-gradient-to-r from-indigo-950/40 via-[#1E293B] to-indigo-950/40 border border-indigo-500/20 flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-2xl bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center text-indigo-400 text-2xl shrink-0">
              <i className="fa-solid fa-user-graduate" />
            </div>
            <div>
              <h3 className="text-xl font-bold text-white">Need guidance from an industry expert?</h3>
              <p className="text-sm text-[#94A3B8] mt-1">
                Share your mock interview results with verified engineering leaders for 1-on-1 mentorship.
              </p>
            </div>
          </div>
          <Link
            to="/mentors"
            className="px-6 py-3 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-sm shadow-[0_0_20px_rgba(99,102,241,0.3)] transition-all shrink-0"
          >
            Explore Mentors
          </Link>
        </div>
      </main>
    </div>
  );
}
