import { useState, useEffect, useRef } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { getInterview, getFeedback, generateFeedback, getInterviewTurns, deleteInterview } from "../api/interviewApi";
import { getInterviewRecording, deleteInterviewRecording } from "../utils/recordingStorage";
import { useAuth } from "../context/AuthContext";

export default function InterviewFeedback() {
  const { id: interviewId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isMentor = user?.role === "MENTOR";

  const [interview, setInterview] = useState(null);
  const [feedback, setFeedback] = useState(null);
  const [turns, setTurns] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState("");

  // Recording player
  const [recordingUrl, setRecordingUrl] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [playbackRate, setPlaybackRate] = useState(1);
  const videoPlaybackRef = useRef(null);

  useEffect(() => {
    let active = true;

    async function loadData() {
      try {
        const [interviewData, turnsData] = await Promise.all([
          getInterview(interviewId),
          getInterviewTurns(interviewId),
        ]);
        if (!active) return;
        setInterview(interviewData);
        setTurns(turnsData || []);

        // Attempt to fetch existing feedback
        try {
          const feedbackData = await getFeedback(interviewId);
          if (active && feedbackData) {
            setFeedback(feedbackData);
          }
        } catch {
          // Not generated yet
        }

        // Load recording from IndexedDB
        try {
          const blob = await getInterviewRecording(interviewId);
          if (active && blob) {
            const url = URL.createObjectURL(blob);
            setRecordingUrl(url);
          }
        } catch (recErr) {
          console.warn("Could not load recording:", recErr);
        }
      } catch (err) {
        if (!active) return;
        console.error(err);
        setError(err.message || "Failed to load interview report.");
      } finally {
        if (active) setLoading(false);
      }
    }
    loadData();

    return () => {
      active = false;
      if (recordingUrl) URL.revokeObjectURL(recordingUrl);
    };
  }, [interviewId]);

  const handleGenerateFeedback = async () => {
    if (interview?.status !== "COMPLETED") {
      setError("Please complete your interview session in the studio before generating AI evaluation.");
      return;
    }
    setGenerating(true);
    setError("");
    try {
      const response = await generateFeedback(interviewId);
      setFeedback(response);
    } catch (err) {
      console.error(err);
      setError(
        err.message?.includes("not completed") || err.message?.includes("answered any questions")
          ? err.message
          : "AI Evaluation generation encountered an issue. Please try again."
      );
    } finally {
      setGenerating(false);
    }
  };

  const handleDeleteInterview = async () => {
    if (!window.confirm("Are you sure you want to delete this mock interview and all its recordings? This action cannot be undone.")) {
      return;
    }
    setDeleting(true);
    try {
      await deleteInterview(interviewId);
      await deleteInterviewRecording(interviewId);
      navigate(isMentor ? "/mentor/dashboard" : "/candidate/dashboard");
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to delete interview.");
      setDeleting(false);
    }
  };

  const togglePlayback = () => {
    const v = videoPlaybackRef.current;
    if (!v) return;
    if (v.paused) {
      v.play();
      setIsPlaying(true);
    } else {
      v.pause();
      setIsPlaying(false);
    }
  };

  const handleSeek = (e) => {
    const v = videoPlaybackRef.current;
    if (!v) return;
    const targetPercent = parseFloat(e.target.value);
    const validDuration = isFinite(duration) && duration > 0 ? duration : (isFinite(v.duration) ? v.duration : 0);
    if (validDuration > 0) {
      const targetTime = (targetPercent / 100) * validDuration;
      v.currentTime = targetTime;
      setCurrentTime(targetTime);
    }
  };

  const handleSkip = (seconds) => {
    const v = videoPlaybackRef.current;
    if (!v) return;
    const validDuration = isFinite(duration) && duration > 0 ? duration : (isFinite(v.duration) ? v.duration : 9999);
    const newTime = Math.min(validDuration, Math.max(0, (v.currentTime || 0) + seconds));
    v.currentTime = newTime;
    setCurrentTime(newTime);
  };

  const handleSpeedChange = (rate) => {
    const v = videoPlaybackRef.current;
    if (v) v.playbackRate = rate;
    setPlaybackRate(rate);
  };

  const formatTime = (t) => {
    if (!isFinite(t) || isNaN(t) || t < 0) return "0:00";
    const m = Math.floor(t / 60);
    const s = Math.floor(t % 60);
    return `${m}:${s.toString().padStart(2, "0")}`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] text-white flex flex-col items-center justify-center">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <p className="text-sm text-[#94A3B8]">Retrieving session report...</p>
      </div>
    );
  }

  const isCompleted = interview?.status === "COMPLETED";
  const score = feedback?.overallScore ?? 0;
  const answerScore = feedback?.answerQualityRating ?? 0;

  const getScoreColor = (val) => {
    if (val >= 80) return "text-emerald-400 border-emerald-400/30 bg-emerald-500/10";
    if (val >= 60) return "text-amber-400 border-amber-400/30 bg-amber-500/10";
    return "text-rose-400 border-rose-400/30 bg-rose-500/10";
  };

  const calculatedProgress = isFinite(duration) && duration > 0 ? (currentTime / duration) * 100 : 0;

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-5xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        {/* Breadcrumb & Top Navigation */}
        <div className="flex items-center justify-between mb-8 flex-wrap gap-4">
          <Link
            to={isMentor ? "/mentor/dashboard" : "/candidate/dashboard"}
            className="text-xs font-semibold text-[#94A3B8] hover:text-white flex items-center gap-2"
          >
            <i className="fa-solid fa-arrow-left" />
            <span>{isMentor ? "Back to Mentor Dashboard" : "Back to Dashboard"}</span>
          </Link>

          <div className="flex items-center gap-3">
            {isMentor ? (
              <span className="px-4 py-2 rounded-xl bg-indigo-600/20 border border-indigo-500/30 text-indigo-300 font-bold text-xs flex items-center gap-2">
                <i className="fa-solid fa-chalkboard-user" />
                Mentor Review Mode
              </span>
            ) : (
              <>
                <button
                  onClick={handleDeleteInterview}
                  disabled={deleting}
                  className="px-3.5 py-2 rounded-xl bg-red-500/10 hover:bg-red-500 hover:text-white border border-red-500/30 text-red-400 font-bold text-xs transition-all flex items-center gap-2"
                  title="Delete this mock interview"
                >
                  <i className="fa-solid fa-trash-can" />
                  <span>{deleting ? "Deleting..." : "Delete Mock"}</span>
                </button>

                <Link
                  to={`/mentors?interviewId=${interviewId}`}
                  className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-2"
                >
                  <i className="fa-solid fa-chalkboard-user" />
                  <span>Review with a Mentor</span>
                </Link>

                <Link
                  to="/interviews/new"
                  className="px-4 py-2 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs shadow-[0_0_15px_rgba(45,212,191,0.3)] hover:scale-105 transition-all flex items-center gap-2"
                >
                  <i className="fa-solid fa-rotate-right" />
                  <span>Take Another Mock</span>
                </Link>
              </>
            )}
          </div>
        </div>

        {/* Incomplete Interview Alert */}
        {!isCompleted && (
          <div className="mb-8 p-6 rounded-3xl bg-amber-500/10 border border-amber-500/30 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-start gap-3">
              <i className="fa-solid fa-triangle-exclamation text-amber-400 text-2xl mt-0.5" />
              <div>
                <h3 className="text-base font-bold text-white">Interview Not Completed Yet</h3>
                <p className="text-xs text-[#94A3B8] mt-1">
                  This mock session is in status <strong className="text-amber-400">{interview?.status}</strong>. AI Evaluation can only be generated after you finish answering questions in the simulation studio.
                </p>
              </div>
            </div>
            {!isMentor && (
              <Link
                to={`/interviews/${interviewId}/room`}
                className="px-5 py-2.5 rounded-xl bg-amber-500 text-[#0F172A] font-bold text-xs uppercase tracking-wider hover:bg-amber-400 transition-colors shrink-0 text-center"
              >
                Resume Interview Room
              </Link>
            )}
          </div>
        )}

        {/* Title Header */}
        <div className="p-8 rounded-3xl bg-gradient-to-r from-[#1E293B] via-[#0F172A] to-[#1E293B] border border-[#334155] shadow-2xl mb-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="flex items-center gap-2 mb-3">
              <span className="px-3 py-1 rounded-full bg-[#2DD4BF]/10 text-[#2DD4BF] border border-[#2DD4BF]/20 text-xs font-bold uppercase tracking-wider">
                Evaluation Report
              </span>
              <span className="px-3 py-1 rounded-full bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 text-xs font-bold uppercase tracking-wider">
                {interview?.interviewType}
              </span>
              <span className="px-3 py-1 rounded-full bg-[#334155] text-[#94A3B8] text-xs font-bold uppercase tracking-wider">
                {interview?.status}
              </span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-black text-white">
              {interview?.title || interview?.targetRole}
            </h1>
            <p className="text-sm text-[#94A3B8] mt-1 flex items-center gap-2">
              <span className="font-semibold text-white/90">{interview?.targetRole}</span>
              <span>•</span>
              <span>Session with {turns.filter((t) => t.speaker === "AI").length} AI questions asked.</span>
            </p>
          </div>

          {!isMentor && isCompleted && (
            <button
              onClick={handleGenerateFeedback}
              disabled={generating}
              className={`px-6 py-4 rounded-xl font-bold text-sm transition-all active:scale-[0.98] flex items-center gap-2 shrink-0 ${
                feedback
                  ? "bg-[#1E293B] border border-[#2DD4BF]/50 text-[#2DD4BF] hover:bg-[#2DD4BF]/10 shadow-[0_0_15px_rgba(45,212,191,0.2)] hover:scale-105"
                  : "bg-[#2DD4BF] text-[#0F172A] shadow-[0_0_25px_rgba(45,212,191,0.35)] hover:scale-105"
              }`}
              title={feedback ? "Re-analyze this interview with the updated evaluator" : "Generate AI Evaluation"}
            >
              {generating ? (
                <>
                  <i className="fa-solid fa-spinner fa-spin" />
                  <span>{feedback ? "Regenerating Analysis..." : "Generating AI Analysis..."}</span>
                </>
              ) : (
                <>
                  <i className={feedback ? "fa-solid fa-arrows-rotate" : "fa-solid fa-bolt"} />
                  <span>{feedback ? "Regenerate AI Evaluation" : "Generate AI Evaluation"}</span>
                </>
              )}
            </button>
          )}
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-3">
            <i className="fa-solid fa-circle-exclamation text-lg" />
            <span>{error}</span>
          </div>
        )}

        {/* Interview Recording Player */}
        <div className="mb-8 p-6 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-2xl">
          <div className="flex items-center gap-3 mb-5">
            <div className="w-9 h-9 rounded-xl bg-red-500/10 border border-red-500/20 flex items-center justify-center text-red-400">
              <i className="fa-solid fa-video" />
            </div>
            <div>
              <h3 className="font-bold text-white text-base">
                {isMentor ? "Candidate Interview Recording" : "Your Interview Recording"}
              </h3>
              <p className="text-xs text-[#94A3B8]">Review performance — body language, clarity, and delivery</p>
            </div>
          </div>

          {recordingUrl ? (
            <div className="space-y-3">
              {/* Video element */}
              <div className="relative w-full rounded-2xl overflow-hidden bg-black border border-[#334155]">
                <video
                  ref={videoPlaybackRef}
                  src={recordingUrl}
                  className="w-full max-h-[420px] object-contain"
                  onTimeUpdate={() => {
                    const v = videoPlaybackRef.current;
                    if (v) {
                      setCurrentTime(v.currentTime || 0);
                      if ((!isFinite(duration) || duration === 0) && isFinite(v.duration) && v.duration > 0) {
                        setDuration(v.duration);
                      }
                    }
                  }}
                  onLoadedMetadata={() => {
                    const v = videoPlaybackRef.current;
                    if (v) {
                      if (isFinite(v.duration) && v.duration > 0) {
                        setDuration(v.duration);
                      } else {
                        // Fix for WebM blobs in Chromium
                        v.currentTime = 1e101;
                        v.ontimeupdate = function () {
                          this.ontimeupdate = null;
                          if (isFinite(this.duration) && this.duration > 0) {
                            setDuration(this.duration);
                          } else {
                            setDuration(this.currentTime);
                          }
                          this.currentTime = 0;
                        };
                      }
                    }
                  }}
                  onEnded={() => setIsPlaying(false)}
                  onClick={togglePlayback}
                  style={isMentor ? {} : { transform: "scaleX(-1)" }}
                />
                {!isPlaying && (
                  <button
                    onClick={togglePlayback}
                    className="absolute inset-0 flex items-center justify-center bg-black/40 hover:bg-black/30 transition-colors group"
                  >
                    <div className="w-16 h-16 rounded-full bg-[#2DD4BF] flex items-center justify-center shadow-[0_0_30px_rgba(45,212,191,0.5)] group-hover:scale-110 transition-transform">
                      <i className="fa-solid fa-play text-[#0F172A] text-xl ml-1" />
                    </div>
                  </button>
                )}
              </div>

              {/* Custom Controls */}
              <div className="px-1 space-y-2">
                {/* Progress scrubber */}
                <input
                  type="range"
                  min={0}
                  max={100}
                  step={0.1}
                  value={calculatedProgress}
                  onChange={handleSeek}
                  onInput={handleSeek}
                  className="w-full h-1.5 rounded-full accent-[#2DD4BF] cursor-pointer"
                />

                {/* Controls row */}
                <div className="flex items-center justify-between gap-3 flex-wrap">
                  <div className="flex items-center gap-2">
                    {/* Play/Pause */}
                    <button
                      onClick={togglePlayback}
                      className="w-9 h-9 rounded-xl bg-[#0F172A] border border-[#334155] flex items-center justify-center text-white hover:border-[#2DD4BF] transition-colors"
                    >
                      <i className={`fa-solid ${isPlaying ? "fa-pause" : "fa-play"} text-xs`} />
                    </button>

                    {/* Quick Step Buttons */}
                    <button
                      onClick={() => handleSkip(-10)}
                      title="Rewind 10 seconds"
                      className="w-9 h-9 rounded-xl bg-[#0F172A] border border-[#334155] flex items-center justify-center text-[#94A3B8] hover:text-white hover:border-[#2DD4BF] transition-colors"
                    >
                      <i className="fa-solid fa-rotate-left text-xs" />
                    </button>
                    <button
                      onClick={() => handleSkip(10)}
                      title="Forward 10 seconds"
                      className="w-9 h-9 rounded-xl bg-[#0F172A] border border-[#334155] flex items-center justify-center text-[#94A3B8] hover:text-white hover:border-[#2DD4BF] transition-colors"
                    >
                      <i className="fa-solid fa-rotate-right text-xs" />
                    </button>

                    {/* Timecode */}
                    <span className="text-xs font-mono text-[#94A3B8] ml-1">
                      {formatTime(currentTime)} / {formatTime(duration)}
                    </span>
                  </div>

                  <div className="flex items-center gap-1.5">
                    <span className="text-[11px] text-[#94A3B8] mr-1">Speed:</span>
                    {[0.75, 1, 1.25, 1.5, 2].map((rate) => (
                      <button
                        key={rate}
                        onClick={() => handleSpeedChange(rate)}
                        className={`px-2 py-1 rounded-lg text-[11px] font-bold transition-colors ${
                          playbackRate === rate
                            ? "bg-[#2DD4BF] text-[#0F172A]"
                            : "bg-[#0F172A] border border-[#334155] text-[#94A3B8] hover:text-white"
                        }`}
                      >
                        {rate}×
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="py-10 text-center rounded-2xl border border-dashed border-[#334155] bg-[#0F172A]/50">
              <i className="fa-solid fa-video-slash text-3xl text-[#334155] mb-3" />
              <p className="text-sm text-[#94A3B8] font-medium">No recording available</p>
              <p className="text-xs text-[#475569] mt-1">
                Recording is saved automatically when your camera is on during the interview.
              </p>
            </div>
          )}
        </div>

        {/* If feedback hasn't been generated yet and interview is completed */}
        {!feedback && !generating && !isMentor && isCompleted && (
          <div className="p-12 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
            <div className="w-16 h-16 mx-auto rounded-2xl bg-[#2DD4BF]/10 flex items-center justify-center text-[#2DD4BF] text-3xl mb-4">
              <i className="fa-solid fa-chart-line" />
            </div>
            <h3 className="text-xl font-bold text-white mb-2">AI Evaluation Ready to Generate</h3>
            <p className="text-sm text-[#94A3B8] max-w-md mx-auto mb-6">
              Click the button above to generate detailed qualitative feedback, scoring, strengths, and recommended study areas based on your responses.
            </p>
            <button
              onClick={handleGenerateFeedback}
              className="px-6 py-3 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-sm shadow-[0_0_20px_rgba(45,212,191,0.3)] hover:scale-105 transition-all"
            >
              Analyze Interview Now
            </button>
          </div>
        )}

        {/* Mentor sees a note when no feedback exists yet */}
        {!feedback && isMentor && (
          <div className="p-10 text-center bg-[#1E293B]/40 rounded-3xl border border-dashed border-[#334155]">
            <i className="fa-solid fa-hourglass-half text-3xl text-[#334155] mb-3" />
            <h3 className="text-lg font-bold text-white mb-1">No AI Evaluation Yet</h3>
            <p className="text-sm text-[#94A3B8]">The candidate hasn't generated their AI evaluation yet.</p>
          </div>
        )}

        {/* Feedback Content */}
        {feedback && (
          <div className="space-y-8">
            {/* Score Highlights */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {/* Overall Score */}
              <div className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] flex items-center gap-6 shadow-xl">
                <div
                  className={`w-24 h-24 rounded-2xl border flex flex-col items-center justify-center shrink-0 ${getScoreColor(
                    score
                  )}`}
                >
                  <span className="text-3xl font-black">{score}</span>
                  <span className="text-[10px] uppercase font-bold tracking-wider">/ 100</span>
                </div>
                <div>
                  <h3 className="font-bold text-white text-base">Overall Readiness Score</h3>
                  <p className="text-xs text-[#94A3B8] mt-1 leading-relaxed">
                    {feedback.overallReason || "Comprehensive assessment based on communication, technical accuracy, and structure."}
                  </p>
                </div>
              </div>

              {/* Answer Quality Score */}
              <div className="p-6 rounded-3xl bg-[#1E293B] border border-[#334155] flex items-center gap-6 shadow-xl">
                <div
                  className={`w-24 h-24 rounded-2xl border flex flex-col items-center justify-center shrink-0 ${getScoreColor(
                    answerScore
                  )}`}
                >
                  <span className="text-3xl font-black">{answerScore}</span>
                  <span className="text-[10px] uppercase font-bold tracking-wider">/ 100</span>
                </div>
                <div>
                  <h3 className="font-bold text-white text-base">Answer Quality & Depth</h3>
                  <p className="text-xs text-[#94A3B8] mt-1 leading-relaxed">
                    {feedback.answerQualityReason || "Measures completeness, technical precision, and structured explanations."}
                  </p>
                </div>
              </div>
            </div>

            {/* Strengths & Development Areas Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Key Strengths */}
              <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
                <div className="flex items-center gap-2 mb-6 text-emerald-400">
                  <div className="w-8 h-8 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-sm">
                    <i className="fa-solid fa-circle-check" />
                  </div>
                  <h3 className="text-lg font-bold text-white">Key Strengths</h3>
                </div>

                <ul className="space-y-3">
                  {feedback.keyStrengths?.length > 0 ? (
                    feedback.keyStrengths.map((str, idx) => (
                      <li key={idx} className="flex items-start gap-3 text-sm text-[#94A3B8]">
                        <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 mt-2 shrink-0" />
                        <span className="text-white/90">{str}</span>
                      </li>
                    ))
                  ) : (
                    <li className="text-xs text-[#94A3B8]">No strengths detected.</li>
                  )}
                </ul>
              </div>

              {/* Development Areas */}
              <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
                <div className="flex items-center gap-2 mb-6 text-amber-400">
                  <div className="w-8 h-8 rounded-xl bg-amber-500/10 border border-amber-500/20 flex items-center justify-center text-sm">
                    <i className="fa-solid fa-bullseye" />
                  </div>
                  <h3 className="text-lg font-bold text-white">Areas for Improvement</h3>
                </div>

                <ul className="space-y-3">
                  {feedback.developmentAreas?.length > 0 ? (
                    feedback.developmentAreas.map((area, idx) => (
                      <li key={idx} className="flex items-start gap-3 text-sm text-[#94A3B8]">
                        <span className="w-1.5 h-1.5 rounded-full bg-amber-400 mt-2 shrink-0" />
                        <span className="text-white/90">{area}</span>
                      </li>
                    ))
                  ) : (
                    <li className="text-xs text-[#94A3B8]">No critical areas flagged.</li>
                  )}
                </ul>
              </div>
            </div>

            {/* Recommended Practice */}
            {feedback.recommendedPractice?.length > 0 && (
              <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl">
                <div className="flex items-center gap-2 mb-6 text-[#2DD4BF]">
                  <div className="w-8 h-8 rounded-xl bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 flex items-center justify-center text-sm">
                    <i className="fa-solid fa-book-open" />
                  </div>
                  <h3 className="text-lg font-bold text-white">Recommended Practice Roadmap</h3>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                  {feedback.recommendedPractice.map((rec, idx) => (
                    <div
                      key={idx}
                      className="p-4 rounded-2xl bg-[#0F172A] border border-[#334155] text-xs font-semibold text-white/90 flex items-center gap-3"
                    >
                      <span className="w-6 h-6 rounded-lg bg-[#2DD4BF]/10 text-[#2DD4BF] flex items-center justify-center text-xs font-bold shrink-0">
                        {idx + 1}
                      </span>
                      <span>{rec}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}
