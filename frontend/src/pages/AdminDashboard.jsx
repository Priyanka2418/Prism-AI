import { useState, useEffect } from "react";
import AppNavbar from "../components/common/AppNavbar";
import { getPendingMentors, verifyMentor, rejectMentor, getAllMentors, getAllUsers } from "../api/adminApi";

export default function AdminDashboard() {
  const [pendingMentors, setPendingMentors] = useState([]);
  const [allMentors, setAllMentors] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  // Rejection Modal
  const [rejectingId, setRejectingId] = useState(null);
  const [reason, setReason] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    async function loadAdminData() {
      try {
        const [pendingData, mentorsData, usersData] = await Promise.allSettled([
          getPendingMentors(),
          getAllMentors(),
          getAllUsers(),
        ]);
        if (pendingData.status === "fulfilled") {
          setPendingMentors(pendingData.value || []);
        }
        if (mentorsData.status === "fulfilled") {
          setAllMentors(mentorsData.value || []);
        }
        if (usersData.status === "fulfilled") {
          setUsers(usersData.value || []);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadAdminData();
  }, []);

  const handleVerify = async (id) => {
    try {
      await verifyMentor(id);
      setPendingMentors((prev) => prev.filter((m) => m.id !== id));
      const updatedMentors = await getAllMentors();
      setAllMentors(updatedMentors);
    } catch (err) {
      alert(err.message || "Failed to verify mentor.");
    }
  };

  const handleConfirmReject = async (e) => {
    e.preventDefault();
    if (!reason.trim()) return;
    setSubmitting(true);

    try {
      await rejectMentor(rejectingId, reason.trim());
      setPendingMentors((prev) => prev.filter((m) => m.id !== id));
      setRejectingId(null);
      setReason("");
      const updatedMentors = await getAllMentors();
      setAllMentors(updatedMentors);
    } catch (err) {
      alert(err.message || "Failed to reject mentor.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="mb-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-red-500/10 text-red-400 border border-red-500/20 text-xs font-bold uppercase tracking-wider mb-2">
            <i className="fa-solid fa-shield-halved" /> Admin Security Center
          </div>
          <h1 className="text-3xl font-black text-white">
            Mentor Verification Queue
          </h1>
          <p className="text-sm text-[#94A3B8] mt-1">
            Review professional credentials, authenticate identity via LinkedIn, and approve mentors for candidate sessions.
          </p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
          <div className="p-6 rounded-2xl bg-[#1E293B] border border-[#334155]">
            <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
              Pending Applications
            </span>
            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-amber-400">{pendingMentors.length}</span>
              <span className="text-xs text-[#94A3B8]">awaiting review</span>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-[#1E293B] border border-[#334155]">
            <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
              Total Mentors
            </span>
            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-[#2DD4BF]">{allMentors.length}</span>
              <span className="text-xs text-[#94A3B8]">registered mentors</span>
            </div>
          </div>

          <div className="p-6 rounded-2xl bg-[#1E293B] border border-[#334155]">
            <span className="text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
              Registered Users
            </span>
            <div className="mt-3 flex items-baseline gap-2">
              <span className="text-3xl font-extrabold text-white">{users.length}</span>
              <span className="text-xs text-[#94A3B8]">total accounts</span>
            </div>
          </div>
        </div>

        {/* Pending Mentors List */}
        <div className="space-y-4">
          <h2 className="text-lg font-bold text-white flex items-center gap-2">
            <i className="fa-solid fa-clock-rotate-left text-[#2DD4BF]" />
            Applications Awaiting Verification ({pendingMentors.length})
          </h2>

          {loading ? (
            <div className="p-12 text-center bg-[#1E293B]/40 rounded-2xl border border-[#334155]">
              <i className="fa-solid fa-spinner fa-spin text-2xl text-[#2DD4BF] mb-2" />
              <p className="text-xs text-[#94A3B8]">Loading verification requests...</p>
            </div>
          ) : pendingMentors.length === 0 ? (
            <div className="p-12 text-center bg-[#1E293B]/40 rounded-2xl border border-dashed border-[#334155]">
              <i className="fa-solid fa-circle-check text-4xl text-emerald-400 mb-3" />
              <h3 className="text-lg font-bold text-white mb-1">Queue Clear!</h3>
              <p className="text-xs text-[#94A3B8]">
                There are no pending mentor applications requiring verification at this time.
              </p>
            </div>
          ) : (
            pendingMentors.map((mentor) => (
              <div
                key={mentor.id}
                className="p-6 rounded-2xl bg-[#1E293B] border border-[#334155] shadow-lg flex flex-col md:flex-row md:items-center justify-between gap-6"
              >
                <div>
                  <div className="flex items-center gap-3 mb-2">
                    <h3 className="text-lg font-bold text-white">
                      {mentor.displayName || "Applicant"}
                    </h3>
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-amber-500/10 text-amber-400 border border-amber-500/20">
                      {mentor.verificationStatus || "PENDING"}
                    </span>
                  </div>

                  <p className="text-xs text-[#94A3B8]">
                    <strong className="text-white">{mentor.jobTitle}</strong> at{" "}
                    <strong className="text-white">{mentor.company}</strong> •{" "}
                    {mentor.yearsOfExperience} years experience
                  </p>

                  {mentor.linkedinUrl && (
                    <div className="mt-3">
                      <a
                        href={mentor.linkedinUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="inline-flex items-center gap-1.5 text-xs font-semibold text-[#2DD4BF] hover:underline"
                      >
                        <i className="fa-brands fa-linkedin text-sm" />
                        <span>Inspect Verified LinkedIn Profile</span>
                      </a>
                    </div>
                  )}
                </div>

                <div className="flex items-center gap-3 shrink-0">
                  <button
                    type="button"
                    onClick={() => setRejectingId(mentor.id)}
                    className="px-4 py-2.5 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 hover:bg-red-500 hover:text-white text-xs font-bold transition-all"
                  >
                    Reject
                  </button>
                  <button
                    type="button"
                    onClick={() => handleVerify(mentor.id)}
                    className="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-[#0F172A] font-bold text-xs shadow-lg transition-all"
                  >
                    Verify & Approve
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      </main>

      {/* Reject Modal */}
      {rejectingId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
          <div className="w-full max-w-md bg-[#1E293B] border border-[#334155] rounded-3xl p-6 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-2">Reject Mentor Application</h3>
            <p className="text-xs text-[#94A3B8] mb-4">
              Enter the reason for rejection (e.g. invalid LinkedIn link, insufficient experience):
            </p>

            <form onSubmit={handleConfirmReject} className="space-y-4">
              <textarea
                rows={3}
                required
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Reason for rejection..."
                className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white text-xs outline-none focus:border-[#2DD4BF] resize-none"
              />

              <div className="flex items-center justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setRejectingId(null)}
                  className="px-4 py-2 rounded-xl border border-[#334155] text-xs text-[#94A3B8] font-bold hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting || !reason.trim()}
                  className="px-5 py-2 rounded-xl bg-red-500 text-white text-xs font-bold shadow-lg hover:bg-red-600 disabled:opacity-50"
                >
                  {submitting ? "Rejecting..." : "Confirm Rejection"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
