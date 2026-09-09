import { useState, useEffect, useRef, useCallback } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import {
  getInterview,
  startInterview,
  startFirstTurn,
  submitAnswer,
  getInterviewTurns,
  completeInterview,
} from "../api/interviewApi";
import { saveInterviewRecording } from "../utils/recordingStorage";

export default function InterviewRoom() {
  const { id: interviewId } = useParams();
  const navigate = useNavigate();

  const [interview, setInterview] = useState(null);
  const [turns, setTurns] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [answerText, setAnswerText] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analyzingStage, setAnalyzingStage] = useState("");
  const [error, setError] = useState("");

  // Speech Recognition (Voice Input)
  const [isListening, setIsListening] = useState(false);
  const recognitionRef = useRef(null);

  // Audio / Speech Synthesis (AI Voice)
  const [isSpeaking, setIsSpeaking] = useState(false);
  const [autoTts, setAutoTts] = useState(true);

  // Webcam Video & Session Recording
  const [cameraEnabled, setCameraEnabled] = useState(true);
  const [isRecording, setIsRecording] = useState(false);
  const videoRef = useRef(null);
  const streamRef = useRef(null);
  const mediaRecorderRef = useRef(null);
  const recordedChunksRef = useRef([]);

  // Timer
  const [secondsRemaining, setSecondsRemaining] = useState(null);
  const [answerDurationSeconds, setAnswerDurationSeconds] = useState(0);

  // Drawer
  const [showHistory, setShowHistory] = useState(false);

  // Speech Synthesis Helper
  const speakText = useCallback((text) => {
    if (!window.speechSynthesis) return;
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = 0.98;
    utterance.pitch = 1.0;
    utterance.onstart = () => setIsSpeaking(true);
    utterance.onend = () => setIsSpeaking(false);
    utterance.onerror = () => setIsSpeaking(false);
    window.speechSynthesis.speak(utterance);
  }, []);

  // Initialize Speech Recognition
  useEffect(() => {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      const recognition = new SpeechRecognition();
      recognition.continuous = true;
      recognition.interimResults = true;
      recognition.lang = "en-US";

      recognition.onresult = (event) => {
        let transcript = "";
        for (let i = event.resultIndex; i < event.results.length; i++) {
          transcript += event.results[i][0].transcript;
        }
        setAnswerText((prev) => {
          const base = prev.trim();
          return base ? `${base} ${transcript}` : transcript;
        });
      };

      recognition.onerror = (err) => {
        console.error("Speech recognition error:", err);
        setIsListening(false);
      };

      recognition.onend = () => {
        setIsListening(false);
      };

      recognitionRef.current = recognition;
    }

    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.stop();
      }
      if (window.speechSynthesis) {
        window.speechSynthesis.cancel();
      }
    };
  }, []);

  // Initialize Webcam & Video/Audio Recording
  useEffect(() => {
    async function startCamera() {
      if (!cameraEnabled) {
        if (mediaRecorderRef.current && mediaRecorderRef.current.state !== "inactive") {
          mediaRecorderRef.current.stop();
        }
        setIsRecording(false);
        if (streamRef.current) {
          streamRef.current.getTracks().forEach((track) => track.stop());
          streamRef.current = null;
        }
        return;
      }
      try {
        let stream;
        try {
          stream = await navigator.mediaDevices.getUserMedia({
            video: { width: { ideal: 640 }, height: { ideal: 480 } },
            audio: true,
          });
        } catch {
          // Fallback to video-only if audio permission differs
          stream = await navigator.mediaDevices.getUserMedia({
            video: { width: { ideal: 640 }, height: { ideal: 480 } },
            audio: false,
          });
        }

        streamRef.current = stream;
        if (videoRef.current) {
          videoRef.current.srcObject = stream;
        }

        // Initialize MediaRecorder for candidate recording
        if (typeof MediaRecorder !== "undefined" && !mediaRecorderRef.current) {
          try {
            let mimeType = "video/webm;codecs=vp8,opus";
            if (!MediaRecorder.isTypeSupported(mimeType)) {
              mimeType = "video/webm";
              if (!MediaRecorder.isTypeSupported(mimeType)) {
                mimeType = "";
              }
            }

            const recorder = mimeType
              ? new MediaRecorder(stream, { mimeType })
              : new MediaRecorder(stream);

            recorder.ondataavailable = (event) => {
              if (event.data && event.data.size > 0) {
                recordedChunksRef.current.push(event.data);
              }
            };

            recorder.start(1000);
            mediaRecorderRef.current = recorder;
            setIsRecording(true);
          } catch (recErr) {
            console.warn("MediaRecorder could not start:", recErr);
          }
        }
      } catch (err) {
        console.warn("Webcam not accessible:", err);
        setCameraEnabled(false);
      }
    }
    startCamera();

    return () => {
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== "inactive") {
        mediaRecorderRef.current.stop();
      }
      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
      }
    };
  }, [cameraEnabled]);

  // Initialize Guard
  const initializedRef = useRef(false);

  // Load Interview & Turns
  useEffect(() => {
    if (initializedRef.current) return;
    initializedRef.current = true;

    async function initSession() {
      try {
        let session = await getInterview(interviewId);

        // Start if CREATED
        if (session.status === "CREATED") {
          session = await startInterview(interviewId);
        }

        if (session.status === "COMPLETED") {
          navigate(`/interviews/${interviewId}/feedback`, { replace: true });
          return;
        }

        setInterview(session);

        // Calculate timer synchronized with expiresAt
        if (session.expiresAt) {
          const expiresMs = new Date(session.expiresAt).getTime();
          const nowMs = Date.now();
          const remainingSecs = Math.max(0, Math.floor((expiresMs - nowMs) / 1000));
          setSecondsRemaining(remainingSecs);
        } else if (session.durationMinutes) {
          setSecondsRemaining(session.durationMinutes * 60);
        }

        // Fetch turns
        let existingTurns = await getInterviewTurns(interviewId);
        if (!existingTurns || existingTurns.length === 0) {
          const firstQuestion = await startFirstTurn(interviewId);
          existingTurns = [firstQuestion];
        }

        setTurns(existingTurns);

        // Find current question turn
        const questions = existingTurns.filter((t) => t.turnType === "QUESTION");
        const latestQuestion = questions[questions.length - 1];
        setCurrentQuestion(latestQuestion);

        if (latestQuestion?.content && autoTts) {
          setTimeout(() => {
            speakText(latestQuestion.content);
          }, 600);
        }
      } catch (err) {
        console.error("Failed to load interview room:", err);
        setError("Unable to connect to interview session. Please refresh.");
      } finally {
        setLoading(false);
      }
    }

    initSession();
  }, [interviewId, navigate, autoTts, speakText]);

  const handleTimerAutoFinish = async () => {
    try {
      await stopAndSaveRecording();
      await completeInterview(interviewId);
      navigate(`/interviews/${interviewId}/feedback`);
    } catch (err) {
      console.error(err);
      navigate(`/interviews/${interviewId}/feedback`);
    }
  };

  // Steady Timer Tick
  useEffect(() => {
    const interval = setInterval(() => {
      setSecondsRemaining((prev) => {
        if (prev === null) return null;
        if (prev <= 1) {
          clearInterval(interval);
          handleTimerAutoFinish();
          return 0;
        }
        return prev - 1;
      });
      setAnswerDurationSeconds((prev) => prev + 1);
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const toggleMic = () => {
    if (!recognitionRef.current) {
      alert("Speech recognition is not supported in this browser. Please use Google Chrome or Edge.");
      return;
    }
    if (isListening) {
      recognitionRef.current.stop();
      setIsListening(false);
    } else {
      recognitionRef.current.start();
      setIsListening(true);
    }
  };

  const stopAndSaveRecording = async () => {
    return new Promise((resolve) => {
      const recorder = mediaRecorderRef.current;
      if (recorder && recorder.state !== "inactive") {
        recorder.onstop = async () => {
          try {
            if (recordedChunksRef.current.length > 0) {
              const mimeType = recorder.mimeType || "video/webm";
              const recordedBlob = new Blob(recordedChunksRef.current, { type: mimeType });
              await saveInterviewRecording(interviewId, recordedBlob);
            }
          } catch (e) {
            console.error("Failed to save interview recording:", e);
          }
          resolve();
        };
        recorder.stop();
      } else if (recordedChunksRef.current.length > 0) {
        const mimeType = recorder?.mimeType || "video/webm";
        const recordedBlob = new Blob(recordedChunksRef.current, { type: mimeType });
        saveInterviewRecording(interviewId, recordedBlob)
          .catch((e) => console.error(e))
          .finally(resolve);
      } else {
        resolve();
      }
    });
  };

  const handleFinishEarly = async () => {
    const confirm = window.confirm(
      "Are you sure you want to end this interview and view your AI feedback and recording?"
    );
    if (!confirm) return;

    try {
      await stopAndSaveRecording();
      await completeInterview(interviewId);
      navigate(`/interviews/${interviewId}/feedback`);
    } catch (err) {
      console.error(err);
      navigate(`/interviews/${interviewId}/feedback`);
    }
  };

  const handleSubmitAnswer = async (e) => {
    e.preventDefault();
    if (!answerText.trim() || !currentQuestion || submitting || isAnalyzing) return;

    if (isListening && recognitionRef.current) {
      recognitionRef.current.stop();
      setIsListening(false);
    }
    if (window.speechSynthesis) {
      window.speechSynthesis.cancel();
    }

    setSubmitting(true);
    setIsAnalyzing(true);
    setAnalyzingStage("Analyzing response & evaluating depth...");
    setError("");

    const answerPayload = answerText.trim();
    const durationPayload = Math.max(1, answerDurationSeconds);

    setAnswerText("");
    setAnswerDurationSeconds(0);

    try {
      // Send answer to backend
      const nextTurnPromise = submitAnswer(interviewId, currentQuestion.id, {
        content: answerPayload,
        answerDurationSeconds: durationPayload,
      });

      // Realistic 2-3 second conversational pause before formulating next question
      setTimeout(() => {
        setAnalyzingStage("Formulating next question...");
      }, 1200);

      const [nextTurn] = await Promise.all([
        nextTurnPromise,
        new Promise((resolve) => setTimeout(resolve, 2400)),
      ]);

      // Refresh turns
      const updatedTurns = await getInterviewTurns(interviewId);
      setTurns(updatedTurns);

      if (nextTurn.aiAction === "COMPLETE" || nextTurn.aiAction === "END_INTERVIEW") {
        await stopAndSaveRecording();
        await completeInterview(interviewId);
        navigate(`/interviews/${interviewId}/feedback`);
        return;
      }

      setIsAnalyzing(false);
      setCurrentQuestion(nextTurn);

      if (nextTurn.content && autoTts) {
        setTimeout(() => {
          speakText(nextTurn.content);
        }, 300);
      }
    } catch (err) {
      console.error(err);
      setIsAnalyzing(false);
      setError(err.message || "Unable to formulate next question. Please submit again.");
    } finally {
      setSubmitting(false);
    }
  };

  const formatTimer = (totalSeconds) => {
    if (totalSeconds === null) return "--:--";
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] text-white flex flex-col items-center justify-center">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <h2 className="text-xl font-bold">Joining AI Simulation Studio...</h2>
        <p className="text-sm text-[#94A3B8] mt-1">Preparing adaptive interview context.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0A0910] text-white flex flex-col h-screen overflow-hidden">
      {/* Top Session Bar */}
      <header className="h-16 px-6 bg-[#0F172A] border-b border-[#334155]/80 flex items-center justify-between z-30 shrink-0">
        <div className="flex items-center gap-4">
          <Link
            to="/candidate/dashboard"
            className="text-xs text-[#94A3B8] hover:text-white flex items-center gap-1.5"
          >
            <i className="fa-solid fa-arrow-left" />
            <span className="hidden sm:inline">Dashboard</span>
          </Link>

          <div className="h-4 w-[1px] bg-[#334155]" />

          <div>
            <div className="flex items-center gap-2">
              <span className="text-sm font-bold text-white line-clamp-1">
                {interview?.title || interview?.targetRole}
              </span>
              <span className="px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-[#2DD4BF]/10 text-[#2DD4BF] border border-[#2DD4BF]/20">
                {interview?.interviewType}
              </span>
              <span className="hidden sm:inline-block px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider bg-amber-500/10 text-amber-400 border border-amber-500/20">
                {currentQuestion?.difficulty || interview?.interviewDifficulty}
              </span>
            </div>
          </div>
        </div>

        {/* Center Timer */}
        <div className="flex items-center gap-4">
          <div className="px-3.5 py-1.5 rounded-full bg-[#1E293B] border border-[#334155] flex items-center gap-2 text-sm font-mono font-bold text-[#2DD4BF]">
            <i className="fa-solid fa-clock text-xs" />
            <span>{formatTimer(secondsRemaining)}</span>
          </div>
        </div>

        {/* Right Controls */}
        <div className="flex items-center gap-3">
          <button
            onClick={() => setShowHistory(!showHistory)}
            className={`px-3 py-1.5 rounded-lg border text-xs font-semibold flex items-center gap-1.5 transition-colors ${
              showHistory
                ? "bg-[#2DD4BF]/10 border-[#2DD4BF] text-[#2DD4BF]"
                : "border-[#334155] text-[#94A3B8] hover:text-white"
            }`}
          >
            <i className="fa-solid fa-list-ol" />
            <span className="hidden md:inline">History ({turns.filter((t) => t.speaker === "AI").length})</span>
          </button>

          <button
            onClick={handleFinishEarly}
            className="px-3.5 py-1.5 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 hover:bg-red-500 hover:text-white text-xs font-bold transition-all"
          >
            End Interview
          </button>
        </div>
      </header>

      {/* Main Studio Workspace */}
      <div className="flex-1 grid grid-cols-1 lg:grid-cols-12 overflow-hidden relative">
        {/* LEFT / TOP: AI Interviewer Console (6 cols) */}
        <div className="lg:col-span-6 p-6 flex flex-col justify-between border-b lg:border-b-0 lg:border-r border-[#334155]/60 bg-gradient-to-b from-[#0F172A] to-[#0A0910] overflow-y-auto">
          <div>
            {/* AI Avatar & Speaker Status */}
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center gap-3">
                <div className="relative">
                  <div
                    className={`w-12 h-12 rounded-2xl flex items-center justify-center text-xl text-[#0F172A] font-bold shadow-lg transition-transform ${
                      isSpeaking
                        ? "bg-[#2DD4BF] scale-105 ring-4 ring-[#2DD4BF]/30 animate-pulse"
                        : isAnalyzing
                        ? "bg-amber-400 scale-105 ring-4 ring-amber-400/30 animate-spin"
                        : "bg-[#2DD4BF]"
                    }`}
                  >
                    <i className={`fa-solid ${isAnalyzing ? "fa-circle-notch" : "fa-robot"}`} />
                  </div>
                  {isSpeaking && (
                    <span className="absolute -bottom-1 -right-1 w-4 h-4 rounded-full bg-emerald-400 border-2 border-[#0F172A]" />
                  )}
                </div>
                <div>
                  <h3 className="font-bold text-white text-base">Prism AI Interviewer</h3>
                  <p className="text-xs text-[#94A3B8]">
                    {isAnalyzing ? (
                      <span className="text-amber-400 flex items-center gap-1.5 font-semibold">
                        <i className="fa-solid fa-spinner fa-spin" /> {analyzingStage}
                      </span>
                    ) : isSpeaking ? (
                      <span className="text-[#2DD4BF] flex items-center gap-1.5 font-medium">
                        <i className="fa-solid fa-volume-high animate-bounce" /> Speaking question...
                      </span>
                    ) : (
                      "Listening & evaluating your response"
                    )}
                  </p>
                </div>
              </div>

              {/* TTS Controls */}
              <div className="flex items-center gap-2">
                <button
                  type="button"
                  disabled={isAnalyzing}
                  onClick={() => currentQuestion?.content && speakText(currentQuestion.content)}
                  title="Re-read Question"
                  className="p-2 rounded-xl bg-[#1E293B] border border-[#334155] text-[#94A3B8] hover:text-[#2DD4BF] transition-colors text-xs disabled:opacity-40"
                >
                  <i className="fa-solid fa-volume-high" />
                </button>
                <button
                  type="button"
                  onClick={() => setAutoTts(!autoTts)}
                  className={`px-2.5 py-1.5 rounded-xl border text-[11px] font-semibold transition-colors ${
                    autoTts
                      ? "bg-[#2DD4BF]/10 text-[#2DD4BF] border-[#2DD4BF]/30"
                      : "bg-[#1E293B] text-[#94A3B8] border-[#334155]"
                  }`}
                >
                  Auto-voice: {autoTts ? "ON" : "OFF"}
                </button>
              </div>
            </div>

            {/* Question Card */}
            <div className="p-6 rounded-3xl bg-[#1E293B]/90 border border-[#334155] shadow-2xl relative min-h-[140px] flex flex-col justify-between">
              {isAnalyzing ? (
                <div className="py-6 flex flex-col items-center justify-center text-center space-y-3">
                  <div className="flex items-center gap-1.5">
                    <span className="w-2.5 h-2.5 rounded-full bg-[#2DD4BF] animate-bounce" style={{ animationDelay: "0ms" }} />
                    <span className="w-2.5 h-2.5 rounded-full bg-[#2DD4BF] animate-bounce" style={{ animationDelay: "150ms" }} />
                    <span className="w-2.5 h-2.5 rounded-full bg-[#2DD4BF] animate-bounce" style={{ animationDelay: "300ms" }} />
                  </div>
                  <p className="text-sm font-medium text-[#94A3B8]">{analyzingStage}</p>
                </div>
              ) : (
                <>
                  <div className="flex items-center justify-between gap-2 mb-3">
                    <span className="text-[11px] font-bold text-[#2DD4BF] uppercase tracking-wider">
                      Turn #{currentQuestion?.turnNumber || 1} • {currentQuestion?.topic || "Discussion"}
                    </span>
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-indigo-500/10 text-indigo-300 border border-indigo-500/20">
                      {currentQuestion?.difficulty || "MEDIUM"}
                    </span>
                  </div>

                  <h2 className="text-xl sm:text-2xl font-bold leading-relaxed text-white">
                    {currentQuestion?.content || "Generating next adaptive interview question..."}
                  </h2>
                </>
              )}
            </div>
          </div>

          {/* AI Tips / Turn Guidance */}
          <div className="mt-6 p-4 rounded-2xl bg-[#1E293B]/40 border border-[#334155]/60 text-xs text-[#94A3B8] flex items-start gap-3">
            <i className="fa-solid fa-lightbulb text-[#2DD4BF] text-sm mt-0.5" />
            <div>
              <p className="font-semibold text-white/90">Interviewer Tip</p>
              <p className="mt-0.5">
                Answer directly and clearly. Mention specific tools, architecture decisions, and practical trade-offs you encountered.
              </p>
            </div>
          </div>
        </div>

        {/* RIGHT / BOTTOM: Candidate Response Studio (6 cols) */}
        <div className="lg:col-span-6 p-6 flex flex-col justify-between bg-[#0F172A] overflow-y-auto">
          <div>
            {/* Top Bar: Candidate Video & Controls */}
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
                <span className="text-xs font-bold uppercase tracking-wider text-white">
                  Candidate Studio
                </span>
              </div>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setCameraEnabled(!cameraEnabled)}
                  className={`px-3 py-1.5 rounded-xl border text-xs font-semibold flex items-center gap-1.5 transition-all ${
                    cameraEnabled
                      ? "bg-[#1E293B] border-[#334155] text-white"
                      : "bg-red-500/10 border-red-500/20 text-red-400"
                  }`}
                >
                  <i className={`fa-solid ${cameraEnabled ? "fa-video" : "fa-video-slash"}`} />
                  <span>{cameraEnabled ? "Camera ON" : "Camera OFF"}</span>
                </button>
              </div>
            </div>

            {/* Webcam Preview Box */}
            <div className="relative w-full h-44 sm:h-52 rounded-2xl bg-[#0A0910] border border-[#334155] overflow-hidden mb-6 flex items-center justify-center">
              {cameraEnabled ? (
                <video
                  ref={videoRef}
                  autoPlay
                  playsInline
                  muted
                  className="w-full h-full object-cover transform -scale-x-100"
                />
              ) : (
                <div className="text-center text-[#94A3B8]">
                  <i className="fa-solid fa-video-slash text-3xl mb-2" />
                  <p className="text-xs">Camera is paused</p>
                </div>
              )}

              {/* Status Overlay */}
              <div className="absolute bottom-3 left-3 flex items-center gap-2">
                <div className="px-2.5 py-1 rounded-md bg-[#0F172A]/80 backdrop-blur-md text-[11px] font-medium border border-white/10 flex items-center gap-2">
                  <span className={`w-2 h-2 rounded-full ${isListening ? "bg-red-500 animate-ping" : "bg-emerald-400"}`} />
                  <span>{isListening ? "Microphone Active" : "Mic Ready"}</span>
                </div>
                {isRecording && (
                  <div className="px-2.5 py-1 rounded-md bg-red-600/90 backdrop-blur-md text-[11px] font-bold border border-red-400/30 flex items-center gap-1.5 text-white">
                    <span className="w-2 h-2 rounded-full bg-white animate-pulse" />
                    REC
                  </div>
                )}
              </div>
            </div>

            {/* Answer Box */}
            <form onSubmit={handleSubmitAnswer} className="space-y-4">
              {error && (
                <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs flex items-center gap-2">
                  <i className="fa-solid fa-circle-info" />
                  <span>{error}</span>
                </div>
              )}

              <div className="relative">
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2 flex items-center justify-between">
                  <span>Your Answer</span>
                  <span className="font-mono text-[#2DD4BF]">
                    Duration: {answerDurationSeconds}s
                  </span>
                </label>

                <textarea
                  rows={6}
                  required
                  disabled={isAnalyzing}
                  value={answerText}
                  onChange={(e) => setAnswerText(e.target.value)}
                  placeholder="Speak your answer using the microphone below or type your response here..."
                  className="w-full px-4 py-3 rounded-2xl bg-[#0A0910] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] resize-none text-sm leading-relaxed disabled:opacity-50"
                />
              </div>

              {/* Voice & Submission Buttons */}
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  disabled={isAnalyzing}
                  onClick={toggleMic}
                  className={`flex-1 py-3.5 px-4 rounded-xl border font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2.5 transition-all ${
                    isListening
                      ? "bg-red-500 text-white border-red-400 shadow-[0_0_20px_rgba(239,68,68,0.5)] animate-pulse"
                      : "bg-[#1E293B] text-white border-[#334155] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <i className={`fa-solid ${isListening ? "fa-stop" : "fa-microphone"}`} />
                  <span>{isListening ? "Stop Dictation" : "Voice Answer (Mic)"}</span>
                </button>

                <button
                  type="submit"
                  disabled={submitting || isAnalyzing || !answerText.trim()}
                  className="flex-1 py-3.5 px-4 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-xs uppercase tracking-wider shadow-[0_0_20px_rgba(45,212,191,0.35)] hover:shadow-[0_0_30px_rgba(45,212,191,0.5)] transition-all active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  {isAnalyzing || submitting ? (
                    <>
                      <i className="fa-solid fa-spinner fa-spin" />
                      Interviewer Analyzing...
                    </>
                  ) : (
                    <>
                      <span>Submit Answer</span>
                      <i className="fa-solid fa-paper-plane" />
                    </>
                  )}
                </button>
              </div>
            </form>
          </div>

          <div className="mt-4 text-center">
            <p className="text-[11px] text-[#94A3B8]">
              Prism.AI adapts questions dynamically to your target role and conversational answers.
            </p>
          </div>
        </div>
      </div>

      {/* Questions History Drawer Overlay */}
      {showHistory && (
        <div className="fixed inset-0 z-50 flex justify-end bg-black/60 backdrop-blur-sm">
          <div className="w-full max-w-md bg-[#1E293B] border-l border-[#334155] h-full flex flex-col p-6 shadow-2xl">
            <div className="flex items-center justify-between pb-4 border-b border-[#334155] mb-4">
              <h3 className="font-bold text-white text-base flex items-center gap-2">
                <i className="fa-solid fa-list-ul text-[#2DD4BF]" />
                Questions Asked
              </h3>
              <button
                onClick={() => setShowHistory(false)}
                className="text-[#94A3B8] hover:text-white"
              >
                <i className="fa-solid fa-xmark text-lg" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto space-y-3 pr-1">
              {turns.filter((t) => t.speaker === "AI").length === 0 ? (
                <p className="text-xs text-[#94A3B8]">No questions asked yet.</p>
              ) : (
                turns
                  .filter((t) => t.speaker === "AI")
                  .map((turn, i) => (
                    <div
                      key={turn.id || i}
                      className="p-3.5 rounded-xl border bg-[#0F172A] border-[#2DD4BF]/20"
                    >
                      <div className="flex items-center justify-between text-[10px] uppercase font-bold text-[#94A3B8] mb-1">
                        <span className="text-[#2DD4BF]">Prism AI</span>
                        <span>Q{i + 1} · {turn.topic || turn.difficulty || ""}</span>
                      </div>
                      <p className="text-white/90 text-xs leading-relaxed">{turn.content}</p>
                    </div>
                  ))
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
