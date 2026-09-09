import { useState, useEffect } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { getPublicMentors, requestMentorship } from "../api/mentoringApi";
import { getMyInterviews } from "../api/interviewApi";

export default function MentorDiscovery() {
  const [searchParams] = useSearchParams();
  const preselectedInterviewId = searchParams.get("interviewId");
  const navigate = useNavigate();

  const [mentors, setMentors] = useState([]);
  const [myInterviews, setMyInterviews] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);

  // Booking Modal State
  const [selectedMentor, setSelectedMentor] = useState(null);
  const [interviewId, setInterviewId] = useState(preselectedInterviewId || "");
  const [requestedStartTime, setRequestedStartTime] = useState("");
  const [requestedDurationMinutes, setRequestedDurationMinutes] = useState(45);
  const [requestMessage, setRequestMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [modalError, setModalError] = useState("");
  const [bookingSuccess, setBookingSuccess] = useState(false);

  useEffect(() => {
    async function loadData() {
      try {
        const [mentorsData, interviewsData] = await Promise.allSettled([
          getPublicMentors(),
          getMyInterviews(),
        ]);
        if (mentorsData.status === "fulfilled") {
          setMentors(mentorsData.value || []);
        }
        if (interviewsData.status === "fulfilled") {
          const validInterviews = (interviewsData.value || []).filter(
            (i) => i.status === "COMPLETED" || i.status === "IN_PROGRESS"
          );
          setMyInterviews(validInterviews);
          if (preselectedInterviewId) {
            setInterviewId(preselectedInterviewId);
          }
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [preselectedInterviewId]);

  const filteredMentors = mentors.filter((m) => {
    const query = searchQuery.toLowerCase();
    const nameMatch = m.displayName?.toLowerCase().includes(query);
    const companyMatch = m.company?.toLowerCase().includes(query);
    const expertiseMatch = m.expertise?.some((e) => e.toLowerCase().includes(query));
    return nameMatch || companyMatch || expertiseMatch;
  });

  const handleOpenBooking = (mentor) => {
    setSelectedMentor(mentor);
    setModalError("");
    setBookingSuccess(false);

    // Set default tomorrow at 10 AM
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(10, 0, 0, 0);
    // Format YYYY-MM-DDTHH:mm
    const year = tomorrow.getFullYear();
    const month = String(tomorrow.getMonth() + 1).padStart(2, "0");
    const day = String(tomorrow.getDate()).padStart(2, "0");
    const hours = String(tomorrow.getHours()).padStart(2, "0");
    const minutes = String(tomorrow.getMinutes()).padStart(2, "0");
    setRequestedStartTime(`${year}-${month}-${day}T${hours}:${minutes}`);
  };

  const handleSendRequest = async (e) => {
    e.preventDefault();
    if (!requestedStartTime) {
      setModalError("Please choose a future date and time.");
      return;
    }

    setSubmitting(true);
    setModalError("");

    try {
      const formattedStartTime =
        requestedStartTime.length === 16
          ? `${requestedStartTime}:00`
          : requestedStartTime;

      await requestMentorship(selectedMentor.id, {
        interviewId: interviewId || null,
        requestedStartTime: formattedStartTime,
        requestedDurationMinutes: Number(requestedDurationMinutes),
        requestMessage: requestMessage.trim(),
      });

      setBookingSuccess(true);
      setTimeout(() => {
        setSelectedMentor(null);
        navigate("/candidate/mentoring");
      }, 1500);
    } catch (err) {
      console.error(err);
      setModalError(err.message || "Failed to send mentoring request. Please check the date & time.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {/* Header */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-8">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 text-xs font-bold uppercase tracking-wider mb-2">
              <i className="fa-solid fa-handshake" /> Verified Mentors
            </div>
            <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
              Connect with Industry Mentors
            </h1>
            <p className="text-sm text-[#94A3B8] mt-1">
              Share your AI mock interview evaluations with verified tech leaders for personalized guidance.
            </p>
          </div>

          <div className="w-full md:w-80">
            <div className="relative">
              <i className="fa-solid fa-magnifying-glass absolute left-4 top-1/2 -translate-y-1/2 text-[#94A3B8] text-sm" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by company, skill, name..."
                className="w-full pl-11 pr-4 py-3 rounded-xl bg-[#1E293B] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] text-sm"
              />
            </div>
          </div>
        </div>

        {/* Mentor Directory */}
        {loading ? (
          <div className="p-16 text-center bg-[#1E293B]/40 rounded-3xl border border-[#334155]">
            <i className="fa-solid fa-spinner fa-spin text-3xl text-[#2DD4BF] mb-3" />
            <p className="text-[#94A3B8]">Finding industry mentors...</p>
          </div>
        ) : filteredMentors.length === 0 ? (
          <div className="p-16 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
            <i className="fa-solid fa-users-slash text-4xl text-[#94A3B8] mb-4" />
            <h3 className="text-lg font-bold text-white mb-1">No Mentors Found</h3>
            <p className="text-sm text-[#94A3B8] max-w-sm mx-auto">
              {searchQuery
                ? "Try searching for a different technology, company, or keyword."
                : "No verified mentors are currently public. Check back soon!"}
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredMentors.map((mentor) => (
              <div
                key={mentor.id}
                className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] hover:border-[#2DD4BF]/40 transition-all flex flex-col justify-between shadow-xl group"
              >
                <div>
                  <div className="flex items-start gap-4 mb-4">
                    <div className="w-14 h-14 rounded-2xl bg-gradient-to-tr from-[#2DD4BF] to-indigo-600 flex items-center justify-center text-xl font-bold text-[#0F172A] shadow-lg shrink-0 overflow-hidden">
                      {mentor.profileImageUrl ? (
                        <img
                          src={mentor.profileImageUrl}
                          alt={mentor.displayName}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        mentor.displayName?.charAt(0) || "M"
                      )}
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <h3 className="font-bold text-white text-base group-hover:text-[#2DD4BF] transition-colors line-clamp-1">
                          {mentor.displayName}
                        </h3>
                        <i className="fa-solid fa-circle-check text-xs text-[#2DD4BF]" title="Verified Mentor" />
                      </div>
                      <p className="text-xs text-[#94A3B8] font-medium">
                        {mentor.jobTitle} • {mentor.company}
                      </p>
                      <span className="inline-block text-[11px] text-[#2DD4BF] font-semibold mt-0.5">
                        {mentor.yearsOfExperience} years experience
                      </span>
                    </div>
                  </div>

                  {mentor.headline && (
                    <p className="text-xs text-white/80 italic mb-3 line-clamp-2">
                      "{mentor.headline}"
                    </p>
                  )}

                  {mentor.bio && (
                    <p className="text-xs text-[#94A3B8] leading-relaxed mb-4 line-clamp-3">
                      {mentor.bio}
                    </p>
                  )}

                  {/* Expertise Chips */}
                  <div className="flex flex-wrap gap-1.5 mb-6">
                    {mentor.expertise?.slice(0, 4).map((skill, i) => (
                      <span
                        key={i}
                        className="text-[11px] px-2.5 py-0.5 rounded-lg bg-[#0F172A] text-[#94A3B8] border border-[#334155]"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="pt-4 border-t border-[#334155] flex items-center justify-between">
                  {mentor.linkedinUrl ? (
                    <a
                      href={mentor.linkedinUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-xs text-[#94A3B8] hover:text-[#2DD4BF] flex items-center gap-1.5"
                    >
                      <i className="fa-brands fa-linkedin text-sm" />
                      <span>LinkedIn</span>
                    </a>
                  ) : (
                    <span />
                  )}

                  <button
                    type="button"
                    onClick={() => handleOpenBooking(mentor)}
                    className="px-4 py-2 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-[0_0_15px_rgba(45,212,191,0.25)] hover:shadow-[0_0_20px_rgba(45,212,191,0.4)] transition-all flex items-center gap-1.5"
                  >
                    <span>Request Session</span>
                    <i className="fa-solid fa-calendar-plus" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Booking Modal */}
      {selectedMentor && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/75 backdrop-blur-sm">
          <div className="w-full max-w-lg bg-[#1E293B] border border-[#334155] rounded-3xl p-6 sm:p-8 shadow-2xl relative">
            <button
              onClick={() => setSelectedMentor(null)}
              className="absolute right-6 top-6 text-[#94A3B8] hover:text-white"
            >
              <i className="fa-solid fa-xmark text-lg" />
            </button>

            <div className="mb-6">
              <h3 className="text-xl font-bold text-white">
                Request Mentoring with {selectedMentor.displayName}
              </h3>
              <p className="text-xs text-[#94A3B8] mt-1">
                {selectedMentor.jobTitle} at {selectedMentor.company}
              </p>
            </div>

            {bookingSuccess ? (
              <div className="p-8 text-center bg-emerald-500/10 border border-emerald-500/20 rounded-2xl text-emerald-400">
                <i className="fa-solid fa-circle-check text-4xl mb-3" />
                <h4 className="text-lg font-bold text-white">Request Sent!</h4>
                <p className="text-xs text-[#94A3B8] mt-1">
                  Your mentorship request has been submitted. Redirecting to sessions...
                </p>
              </div>
            ) : (
              <form onSubmit={handleSendRequest} className="space-y-4">
                {modalError && (
                  <div className="p-3 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-xs">
                    {modalError}
                  </div>
                )}

                {/* Interview Selection */}
                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8]">
                      Mock Interview to Review <span className="text-gray-400 font-normal lowercase">(optional)</span>
                    </label>
                    {interviewId && (
                      <button
                        type="button"
                        onClick={() => setInterviewId("")}
                        className="text-[11px] text-[#2DD4BF] hover:underline"
                      >
                        Remove Interview
                      </button>
                    )}
                  </div>
                  <select
                    value={interviewId}
                    onChange={(e) => setInterviewId(e.target.value)}
                    className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF] text-xs"
                  >
                    <option value="">-- None (General Mentoring / Career Advice) --</option>
                    {myInterviews.map((item) => (
                      <option key={item.id} value={item.id}>
                        {item.targetRole} ({item.interviewType} • {item.status})
                      </option>
                    ))}
                  </select>
                  {myInterviews.length === 0 ? (
                    <p className="text-[11px] text-[#94A3B8] mt-1.5">
                      No interviews attached. You can proceed with general mentoring, or{" "}
                      <Link to="/interviews/new" className="text-[#2DD4BF] underline">
                        take a mock interview
                      </Link>{" "}
                      first to share with your mentor.
                    </p>
                  ) : (
                    <p className="text-[10px] text-[#94A3B8]/70 mt-1">
                      Attaching an interview lets your mentor review your answers, score breakdown, and AI feedback.
                    </p>
                  )}
                </div>

                {/* Date & Time */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-1.5">
                      Requested Date & Time *
                    </label>
                    <input
                      type="datetime-local"
                      required
                      value={requestedStartTime}
                      onChange={(e) => setRequestedStartTime(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF] text-xs"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-1.5">
                      Duration (Mins) *
                    </label>
                    <select
                      value={requestedDurationMinutes}
                      onChange={(e) => setRequestedDurationMinutes(e.target.value)}
                      className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF] text-xs"
                    >
                      <option value="30">30 minutes</option>
                      <option value="45">45 minutes</option>
                      <option value="60">60 minutes</option>
                      <option value="90">90 minutes</option>
                    </select>
                  </div>
                </div>

                {/* Note */}
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-1.5">
                    Message / Focus Areas
                  </label>
                  <textarea
                    rows={3}
                    value={requestMessage}
                    onChange={(e) => setRequestMessage(e.target.value)}
                    placeholder="Describe what specific areas you'd like guidance on (e.g. system design trade-offs, behavioral examples)..."
                    className="w-full px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] text-xs resize-none"
                  />
                </div>

                <div className="pt-2 flex items-center justify-end gap-3">
                  <button
                    type="button"
                    onClick={() => setSelectedMentor(null)}
                    className="px-4 py-2.5 rounded-xl border border-[#334155] text-[#94A3B8] text-xs font-bold hover:text-white"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={submitting}
                    className="px-6 py-2.5 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-lg transition-all active:scale-95 disabled:opacity-50 flex items-center gap-2"
                  >
                    {submitting ? (
                      <>
                        <i className="fa-solid fa-spinner fa-spin" />
                        Sending...
                      </>
                    ) : (
                      <>
                        <span>Send Mentoring Request</span>
                        <i className="fa-solid fa-paper-plane" />
                      </>
                    )}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
