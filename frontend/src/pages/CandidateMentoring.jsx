import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { getCandidateMentorRequests, getCandidateSessions, deleteSession } from "../api/mentoringApi";

export default function CandidateMentoring() {
  const [requests, setRequests] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("sessions");
  const [deletingSessionId, setDeletingSessionId] = useState(null);

  useEffect(() => {
    async function loadData() {
      try {
        const [requestsData, sessionsData] = await Promise.allSettled([
          getCandidateMentorRequests(),
          getCandidateSessions(),
        ]);
        if (requestsData.status === "fulfilled") {
          setRequests(requestsData.value || []);
        }
        if (sessionsData.status === "fulfilled") {
          setSessions(sessionsData.value || []);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  const handleDeleteSession = async (sessionId) => {
    if (!window.confirm("Are you sure you want to delete this mentoring session and its conversation?")) return;
    setDeletingSessionId(sessionId);
    try {
      await deleteSession(sessionId);
      setSessions((prev) => prev.filter((s) => s.id !== sessionId));
    } catch (err) {
      console.error(err);
      alert(err.message || "Failed to delete session.");
    } finally {
      setDeletingSessionId(null);
    }
  };

  const requestStatusColor = {
    PENDING: "bg-amber-500/10 text-amber-400 border-amber-500/20",
    ACCEPTED: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20",
    REJECTED: "bg-rose-500/10 text-rose-400 border-rose-500/20",
  };

  const sessionStatusColor = {
    SCHEDULED: "bg-blue-500/10 text-blue-400 border-blue-500/20",
    ACTIVE: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20 animate-pulse",
    COMPLETED: "bg-slate-500/10 text-slate-400 border-slate-500/20",
    CANCELLED: "bg-rose-500/10 text-rose-400 border-rose-500/20",
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-6xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
          <div>
            <h1 className="text-3xl font-black tracking-tight text-white">
              My Mentorship Hub
            </h1>
            <p className="text-sm text-[#94A3B8] mt-1">
              Track your outgoing requests and join scheduled 1-on-1 expert mentoring sessions.
            </p>
          </div>

          <Link
            to="/mentors"
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-lg hover:scale-105 transition-all self-start"
          >
            <i className="fa-solid fa-user-plus" />
            <span>Find More Mentors</span>
          </Link>
        </div>

        {/* Tabs */}
        <div className="flex items-center gap-3 border-b border-[#334155] mb-8 pb-3">
          <button
            onClick={() => setActiveTab("sessions")}
            className={`pb-2 px-3 text-sm font-bold transition-all relative ${
              activeTab === "sessions"
                ? "text-[#2DD4BF]"
                : "text-[#94A3B8] hover:text-white"
            }`}
          >
            Active Sessions ({sessions.length})
            {activeTab === "sessions" && (
              <span className="absolute bottom-0 left-0 w-full h-0.5 bg-[#2DD4BF] rounded-full" />
            )}
          </button>

          <button
            onClick={() => setActiveTab("requests")}
            className={`pb-2 px-3 text-sm font-bold transition-all relative ${
              activeTab === "requests"
                ? "text-[#2DD4BF]"
                : "text-[#94A3B8] hover:text-white"
            }`}
          >
            Mentoring Requests ({requests.length})
            {activeTab === "requests" && (
              <span className="absolute bottom-0 left-0 w-full h-0.5 bg-[#2DD4BF] rounded-full" />
            )}
          </button>
        </div>

        {loading ? (
          <div className="p-16 text-center bg-[#1E293B]/40 rounded-3xl border border-[#334155]">
            <i className="fa-solid fa-spinner fa-spin text-3xl text-[#2DD4BF] mb-3" />
            <p className="text-[#94A3B8]">Loading mentorship details...</p>
          </div>
        ) : activeTab === "sessions" ? (
          /* SESSIONS TAB */
          sessions.length === 0 ? (
            <div className="p-16 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
              <i className="fa-solid fa-calendar-xmark text-4xl text-[#94A3B8] mb-3" />
              <h3 className="text-lg font-bold text-white mb-1">No Active Sessions</h3>
              <p className="text-xs text-[#94A3B8] max-w-sm mx-auto mb-6">
                When a mentor accepts your request, the session and real-time chat room will appear here.
              </p>
              <Link
                to="/mentors"
                className="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs shadow-lg transition-all inline-block"
              >
                Browse Mentors
              </Link>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {sessions.map((session) => (
                <div
                  key={session.id}
                  className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] hover:border-[#2DD4BF]/40 transition-all flex flex-col justify-between shadow-xl"
                >
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <span
                        className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full border ${
                          sessionStatusColor[session.status] || sessionStatusColor.SCHEDULED
                        }`}
                      >
                        {session.status}
                      </span>
                      <span className="text-xs text-[#94A3B8] flex items-center gap-1.5">
                        <i className="fa-solid fa-clock" />
                        {new Date(session.scheduledStartAt).toLocaleDateString(undefined, {
                          month: "short",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </span>
                    </div>

                    <h3 className="text-lg font-bold text-white mb-1">
                      Mentor: {session.otherParticipantName || "Mentor"}
                    </h3>
                    <p className="text-xs text-[#94A3B8]">
                      End Time: {new Date(session.scheduledEndAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                    </p>
                  </div>

                  <div className="mt-6 pt-4 border-t border-[#334155] flex items-center justify-between gap-3">
                    <button
                      onClick={() => handleDeleteSession(session.id)}
                      disabled={deletingSessionId === session.id}
                      className="px-3 py-2 rounded-xl bg-red-500/10 hover:bg-red-500 hover:text-white border border-red-500/30 text-red-400 font-semibold text-xs transition-all flex items-center gap-1.5"
                      title="Delete this mentoring session"
                    >
                      <i className="fa-solid fa-trash-can text-[11px]" />
                      <span>{deletingSessionId === session.id ? "Deleting..." : "Delete"}</span>
                    </button>

                    <Link
                      to={`/sessions/${session.id}`}
                      className="px-4 py-2 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-[0_0_15px_rgba(45,212,191,0.25)] hover:scale-105 transition-all flex items-center gap-2"
                    >
                      <span>Join Chat Room</span>
                      <i className="fa-solid fa-comments" />
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )
        ) : (
          /* REQUESTS TAB */
          requests.length === 0 ? (
            <div className="p-16 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
              <i className="fa-solid fa-inbox text-4xl text-[#94A3B8] mb-3" />
              <h3 className="text-lg font-bold text-white mb-1">No Mentoring Requests</h3>
              <p className="text-xs text-[#94A3B8] max-w-sm mx-auto">
                You have not requested any mentorship sessions yet.
              </p>
            </div>
          ) : (
            <div className="space-y-4">
              {requests.map((req) => (
                <div
                  key={req.id}
                  className="p-6 rounded-2xl bg-[#1E293B] border border-[#334155] flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                >
                  <div>
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="font-bold text-white text-base">
                        Request to {req.mentorName || "Mentor"}
                      </h3>
                      <span
                        className={`text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full border ${
                          requestStatusColor[req.status] || requestStatusColor.PENDING
                        }`}
                      >
                        {req.status}
                      </span>
                    </div>

                    <p className="text-xs text-[#94A3B8] flex items-center gap-3">
                      <span>
                        <i className="fa-solid fa-calendar mr-1" />
                        {new Date(req.requestedStartTime).toLocaleString(undefined, {
                          month: "short",
                          day: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </span>
                      <span>•</span>
                      <span>{req.requestedDurationMinutes} mins</span>
                    </p>

                    {req.requestMessage && (
                      <p className="text-xs text-white/80 mt-2 italic bg-[#0F172A] p-2 rounded-lg border border-[#334155]">
                        "{req.requestMessage}"
                      </p>
                    )}

                    {req.mentorRejectionReason && (
                      <p className="text-xs text-rose-400 mt-2 font-medium">
                        Rejection reason: {req.mentorRejectionReason}
                      </p>
                    )}
                  </div>

                  <span className="text-[11px] text-[#94A3B8] self-end sm:self-center">
                    Sent {new Date(req.createdAt).toLocaleDateString()}
                  </span>
                </div>
              ))}
            </div>
          )
        )}
      </main>
    </div>
  );
}
