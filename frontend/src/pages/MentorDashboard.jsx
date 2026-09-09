import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { useAuth } from "../context/AuthContext";
import {
  getMentorProfile,
  getMentorPublicProfile,
} from "../api/profileApi";
import {
  getMentorPendingRequests,
  acceptMentorRequest,
  rejectMentorRequest,
  getMentorSessions,
  deleteSession,
} from "../api/mentoringApi";

export default function MentorDashboard() {
  const { user, mentorProfile, setMentorProfile } = useAuth();
  const navigate = useNavigate();

  const [profile, setProfile] = useState(mentorProfile);
  const [requests, setRequests] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deletingSessionId, setDeletingSessionId] = useState(null);

  // Reject Modal State
  const [rejectingRequestId, setRejectingRequestId] = useState(null);
  const [rejectionReason, setRejectionReason] = useState("");
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    async function loadData() {
      try {
        let p = mentorProfile;
        if (!p) {
          try {
            p = await getMentorProfile();
            setMentorProfile(p);
            setProfile(p);
          } catch {
            navigate("/mentor/profile", { replace: true });
            return;
          }
        } else {
          setProfile(p);
        }

        const [reqsData, sessData] = await Promise.allSettled([
          getMentorPendingRequests(),
          getMentorSessions(),
        ]);

        if (reqsData.status === "fulfilled") {
          setRequests(reqsData.value || []);
        }
        if (sessData.status === "fulfilled") {
          setSessions(sessData.value || []);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [mentorProfile, navigate, setMentorProfile]);

  const handleAccept = async (requestId) => {
    try {
      await acceptMentorRequest(requestId);
      setRequests((prev) => prev.filter((r) => r.id !== requestId));
      const updatedSessions = await getMentorSessions();
      setSessions(updatedSessions);
    } catch (err) {
      alert(err.message || "Failed to accept request.");
    }
  };

  const handleConfirmReject = async (e) => {
    e.preventDefault();
    if (!rejectionReason.trim()) return;
    setProcessing(true);

    try {
      await rejectMentorRequest(rejectingRequestId, rejectionReason.trim());
      setRequests((prev) => prev.filter((r) => r.id !== rejectingRequestId));
      setRejectingRequestId(null);
      setRejectionReason("");
    } catch (err) {
      alert(err.message || "Failed to reject request.");
    } finally {
      setProcessing(false);
    }
  };

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

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] text-white flex flex-col items-center justify-center">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <p className="text-sm text-[#94A3B8]">Loading mentor portal...</p>
      </div>
    );
  }

  const isVerified = profile?.verificationStatus === "VERIFIED";

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {/* Verification Status Banner */}
        {!isVerified ? (
          <div className="mb-8 p-6 rounded-3xl bg-amber-500/10 border border-amber-500/30 text-amber-300 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-2xl bg-amber-500/20 flex items-center justify-center text-amber-400 text-lg shrink-0">
                <i className="fa-solid fa-hourglass-half" />
              </div>
              <div>
                <h3 className="font-bold text-white text-base">
                  Profile Verification Pending
                </h3>
                <p className="text-xs text-amber-300/80 mt-0.5">
                  Platform administrators are reviewing your credentials and LinkedIn profile before opening public candidate bookings.
                </p>
              </div>
            </div>
            <Link
              to="/mentor/profile"
              className="px-4 py-2 rounded-xl bg-amber-500/20 hover:bg-amber-500/30 text-amber-200 border border-amber-500/40 text-xs font-bold transition-all self-start sm:self-center"
            >
              Edit Profile
            </Link>
          </div>
        ) : (
          <div className="mb-8 p-6 rounded-3xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 rounded-2xl bg-emerald-500/20 flex items-center justify-center text-emerald-400 text-lg shrink-0">
                <i className="fa-solid fa-badge-check" />
              </div>
              <div>
                <h3 className="font-bold text-white text-base">Verified Industry Mentor</h3>
                <p className="text-xs text-emerald-300/80 mt-0.5">
                  Your profile is public. Candidates can discover your experience and request 1-on-1 interview feedback.
                </p>
              </div>
            </div>
            <Link
              to="/mentor/profile"
              className="px-4 py-2 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-200 border border-emerald-500/40 text-xs font-bold transition-all self-start sm:self-center"
            >
              Manage Profile
            </Link>
          </div>
        )}

        {/* Welcome Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-black text-white">
            Welcome, {profile?.displayName || user?.email?.split("@")[0]}
          </h1>
          <p className="text-sm text-[#94A3B8] mt-1">
            {profile?.jobTitle} at {profile?.company} • {profile?.yearsOfExperience} yrs experience
          </p>
        </div>

        {/* Requests & Sessions Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Pending Mentoring Requests */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                <i className="fa-solid fa-envelope-open-text text-[#2DD4BF]" />
                Incoming Candidate Requests ({requests.length})
              </h2>
            </div>

            {requests.length === 0 ? (
              <div className="p-8 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
                <i className="fa-solid fa-inbox text-3xl text-[#94A3B8] mb-2" />
                <p className="text-xs text-[#94A3B8]">No pending requests right now.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {requests.map((req) => (
                  <div
                    key={req.id}
                    className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-lg flex flex-col justify-between gap-4"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                          {req.status}
                        </span>
                        <span className="text-xs text-[#94A3B8]">
                          Duration: {req.requestedDurationMinutes} mins
                        </span>
                      </div>

                      <h3 className="font-bold text-white text-base">
                        Candidate Request
                      </h3>
                      <p className="text-xs text-[#94A3B8] mt-1 flex items-center gap-2">
                        <i className="fa-solid fa-clock" />
                        <span>
                          {new Date(req.requestedStartTime).toLocaleString(undefined, {
                            month: "short",
                            day: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          })}
                        </span>
                      </p>

                      {req.requestMessage && (
                        <p className="text-xs text-white/90 mt-3 p-3 rounded-xl bg-[#0F172A] border border-[#334155] italic">
                          "{req.requestMessage}"
                        </p>
                      )}

                      {req.interviewId && (
                        <div className="mt-3">
                          <Link
                            to={`/interviews/${req.interviewId}/feedback`}
                            target="_blank"
                            className="text-xs font-semibold text-[#2DD4BF] hover:underline flex items-center gap-1.5"
                          >
                            <i className="fa-solid fa-file-lines" />
                            <span>Preview Candidate's Mock Evaluation</span>
                          </Link>
                        </div>
                      )}
                    </div>

                    <div className="pt-3 border-t border-[#334155] flex items-center justify-end gap-2">
                      <button
                        type="button"
                        onClick={() => setRejectingRequestId(req.id)}
                        className="px-4 py-2 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 hover:bg-red-500 hover:text-white text-xs font-bold transition-all"
                      >
                        Decline
                      </button>
                      <button
                        type="button"
                        onClick={() => handleAccept(req.id)}
                        className="px-5 py-2 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-md hover:scale-105 transition-all"
                      >
                        Accept Request
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Active / Scheduled Sessions */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-white flex items-center gap-2">
                <i className="fa-solid fa-calendar-check text-[#2DD4BF]" />
                Scheduled Mentoring Sessions ({sessions.length})
              </h2>
            </div>

            {sessions.length === 0 ? (
              <div className="p-8 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
                <i className="fa-solid fa-calendar-xmark text-3xl text-[#94A3B8] mb-2" />
                <p className="text-xs text-[#94A3B8]">No scheduled sessions yet.</p>
              </div>
            ) : (
              <div className="space-y-4">
                {sessions.map((sess) => (
                  <div
                    key={sess.id}
                    className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-lg flex flex-col justify-between gap-4"
                  >
                    <div>
                      <div className="flex items-center justify-between mb-2">
                        <span className="text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                          {sess.status}
                        </span>
                        <span className="text-xs text-[#94A3B8]">
                          {new Date(sess.scheduledStartAt).toLocaleDateString(undefined, {
                            month: "short",
                            day: "numeric",
                          })}
                        </span>
                      </div>

                      <h3 className="font-bold text-white text-base">
                        Candidate: {sess.otherParticipantName || "Student"}
                      </h3>
                      <p className="text-xs text-[#94A3B8] mt-1">
                        Time: {new Date(sess.scheduledStartAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })} - {new Date(sess.scheduledEndAt).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                      </p>
                    </div>

                    <div className="pt-3 border-t border-[#334155] flex items-center justify-between gap-3">
                      <button
                        onClick={() => handleDeleteSession(sess.id)}
                        disabled={deletingSessionId === sess.id}
                        className="px-3 py-1.5 rounded-xl bg-red-500/10 hover:bg-red-500 hover:text-white border border-red-500/30 text-red-400 font-semibold text-xs transition-all flex items-center gap-1.5"
                        title="Delete this mentoring session"
                      >
                        <i className="fa-solid fa-trash-can text-[11px]" />
                        <span>{deletingSessionId === sess.id ? "Deleting..." : "Delete"}</span>
                      </button>

                      <Link
                        to={`/sessions/${sess.id}`}
                        className="px-4 py-2 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-md hover:scale-105 transition-all flex items-center gap-1.5"
                      >
                        <span>Open Chat</span>
                        <i className="fa-solid fa-comments" />
                      </Link>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>

      {/* Decline Reason Modal */}
      {rejectingRequestId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
          <div className="w-full max-w-md bg-[#1E293B] border border-[#334155] rounded-3xl p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-2">Decline Mentoring Request</h3>
            <p className="text-xs text-[#94A3B8] mb-4">
              Please provide a polite reason for declining so the candidate can reschedule or find another mentor.
            </p>

            <form onSubmit={handleConfirmReject} className="space-y-4">
              <textarea
                rows={3}
                required
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                placeholder="e.g. Busy with schedule conflicts this week..."
                className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white text-xs outline-none focus:border-[#2DD4BF] resize-none"
              />

              <div className="flex items-center justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setRejectingRequestId(null)}
                  className="px-4 py-2 rounded-xl border border-[#334155] text-xs text-[#94A3B8] font-bold hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={processing || !rejectionReason.trim()}
                  className="px-5 py-2 rounded-xl bg-red-500 text-white text-xs font-bold shadow-lg hover:bg-red-600 disabled:opacity-50"
                >
                  {processing ? "Declining..." : "Confirm Decline"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
