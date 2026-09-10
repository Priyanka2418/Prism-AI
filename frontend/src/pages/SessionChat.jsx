import { useState, useEffect, useRef } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { useAuth } from "../context/AuthContext";
import { getSession, getChatHistory, sendChatMessage, deleteChatMessage, deleteSession } from "../api/mentoringApi";

export default function SessionChat() {
  const { sessionId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [session, setSession] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [deletingSession, setDeletingSession] = useState(false);
  const [error, setError] = useState("");

  const messagesEndRef = useRef(null);

  const formatISTTime = (isoString) => {
    if (!isoString) return "";
    try {
      const date = new Date(isoString);
      return date.toLocaleTimeString("en-IN", {
        timeZone: "Asia/Kolkata",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
      });
    } catch {
      return "";
    }
  };

  const formatISTDateTime = (isoString) => {
    if (!isoString) return "";
    try {
      const date = new Date(isoString);
      return date.toLocaleString("en-IN", {
        timeZone: "Asia/Kolkata",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
      });
    } catch {
      return "";
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    let intervalId;

    async function loadSessionAndChat() {
      try {
        const [sessionData, chatData] = await Promise.all([
          getSession(sessionId),
          getChatHistory(sessionId),
        ]);
        setSession(sessionData);
        setMessages(chatData || []);
      } catch (err) {
        console.error(err);
        setError(err.message || "Failed to load session chat.");
      } finally {
        setLoading(false);
      }
    }

    loadSessionAndChat();

    // Poll for new messages every 3 seconds
    intervalId = setInterval(async () => {
      try {
        const chatData = await getChatHistory(sessionId);
        if (chatData) {
          setMessages(chatData);
        }
      } catch {
        // silent catch on background poll
      }
    }, 3000);

    return () => clearInterval(intervalId);
  }, [sessionId]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!inputText.trim() || sending) return;

    const content = inputText.trim();
    setInputText("");
    setSending(true);

    try {
      const newMsg = await sendChatMessage(sessionId, content);
      setMessages((prev) => [...prev, newMsg]);
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to send message.");
    } finally {
      setSending(false);
    }
  };

  const handleDeleteMessage = async (msgId) => {
    if (!msgId) return;
    if (!window.confirm("Are you sure you want to delete this message?")) return;

    try {
      await deleteChatMessage(sessionId, msgId);
      setMessages((prev) => prev.filter((m) => m.id !== msgId));
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to delete message.");
    }
  };

  const handleDeleteEntireSession = async () => {
    if (!window.confirm("Are you sure you want to delete this entire mentoring session and chat history?")) return;
    setDeletingSession(true);
    try {
      await deleteSession(sessionId);
      navigate(user?.role === "MENTOR" ? "/mentor/dashboard" : "/candidate/mentoring");
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to delete session.");
      setDeletingSession(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] text-white flex flex-col items-center justify-center">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <p className="text-sm text-[#94A3B8]">Entering session workspace...</p>
      </div>
    );
  }

  const currentUserId = String(user?.id || "").toLowerCase();

  return (
    <div className="min-h-screen bg-[#0A0910] text-white flex flex-col h-screen overflow-hidden">
      <AppNavbar />

      {/* Top Session Header */}
      <div className="px-6 py-3 bg-[#0F172A] border-b border-[#334155] flex items-center justify-between z-20 shrink-0 flex-wrap gap-2">
        <div className="flex items-center gap-3">
          <Link
            to={user?.role === "MENTOR" ? "/mentor/dashboard" : "/candidate/mentoring"}
            className="text-[#94A3B8] hover:text-white text-xs flex items-center gap-1"
          >
            <i className="fa-solid fa-arrow-left" />
            <span className="hidden sm:inline">Back</span>
          </Link>
          <div className="h-4 w-[1px] bg-[#334155]" />
          <div>
            <h2 className="text-sm font-bold text-white flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <span>Session with {session?.otherParticipantName || "Mentor"}</span>
            </h2>
            <p className="text-[11px] text-[#94A3B8]">
              Status: {session?.status} • Scheduled: {formatISTDateTime(session?.scheduledStartAt)} (IST)
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          {session?.interviewId && (
            <Link
              to={`/interviews/${session.interviewId}/feedback`}
              target="_blank"
              className="px-3 py-1.5 rounded-lg bg-[#1E293B] border border-[#334155] text-xs font-semibold text-[#2DD4BF] hover:border-[#2DD4BF]/40 transition-colors flex items-center gap-1.5"
            >
              <i className="fa-solid fa-arrow-up-right-from-square text-[10px]" />
              <span>Open Mock Evaluation</span>
            </Link>
          )}

          {/* Delete Entire Session Option */}
          <button
            onClick={handleDeleteEntireSession}
            disabled={deletingSession}
            className="px-3 py-1.5 rounded-lg bg-red-500/10 hover:bg-red-500 hover:text-white border border-red-500/30 text-red-400 text-xs font-semibold transition-all flex items-center gap-1.5"
            title="Delete this entire session"
          >
            <i className="fa-solid fa-trash-can text-[11px]" />
            <span>{deletingSession ? "Deleting..." : "Delete Session"}</span>
          </button>
        </div>
      </div>

      {/* Chat Messages Area */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4 max-w-4xl w-full mx-auto">
        {messages.length === 0 ? (
          <div className="h-full flex flex-col items-center justify-center text-center p-8 text-[#94A3B8]">
            <div className="w-16 h-16 rounded-2xl bg-[#1E293B] flex items-center justify-center text-2xl text-[#2DD4BF] mb-3">
              <i className="fa-solid fa-comments" />
            </div>
            <h3 className="font-bold text-white text-base">Mentoring Workspace Active</h3>
            <p className="text-xs max-w-sm mt-1">
              Start the discussion! Ask questions about system architecture, behavioral feedback, or career navigation.
            </p>
          </div>
        ) : (
          messages.map((msg, index) => {
            const isMe = String(msg.senderId || "").toLowerCase() === currentUserId;
            return (
              <div
                key={msg.id || index}
                className={`flex flex-col group ${isMe ? "items-end" : "items-start"}`}
              >
                <div className="flex items-center gap-2 max-w-md sm:max-w-lg">
                  {/* Delete button only for user's own message */}
                  {isMe && msg.id && (
                    <button
                      onClick={() => handleDeleteMessage(msg.id)}
                      title="Delete this message"
                      className="p-1.5 text-xs text-[#94A3B8] hover:text-red-400 hover:bg-red-500/10 rounded-lg transition-all"
                    >
                      <i className="fa-solid fa-trash-can" />
                    </button>
                  )}

                  <div
                    className={`rounded-2xl px-4 py-3 text-sm shadow-md leading-relaxed ${
                      isMe
                        ? "bg-[#2DD4BF] text-[#0F172A] font-medium rounded-br-none"
                        : "bg-[#1E293B] text-white border border-[#334155] rounded-bl-none"
                    }`}
                  >
                    <p className="whitespace-pre-wrap">{msg.content}</p>
                  </div>
                </div>

                <span className="text-[10px] text-[#94A3B8] mt-1 px-1">
                  {formatISTTime(msg.createdAt)}
                </span>
              </div>
            );
          })
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Bottom Message Input Bar */}
      <div className="p-4 bg-[#0F172A] border-t border-[#334155] shrink-0">
        <div className="max-w-4xl mx-auto">
          {error && (
            <div className="mb-2 text-xs text-red-400">
              {error}
            </div>
          )}
          <form onSubmit={handleSendMessage} className="flex gap-2">
            <input
              type="text"
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              placeholder="Type your message or technical question..."
              className="flex-1 px-4 py-3 rounded-xl bg-[#0A0910] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] text-sm"
            />
            <button
              type="submit"
              disabled={sending || !inputText.trim()}
              className="px-6 py-3 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-sm shadow-[0_0_15px_rgba(45,212,191,0.3)] hover:scale-105 active:scale-95 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
            >
              {sending ? (
                <i className="fa-solid fa-spinner fa-spin" />
              ) : (
                <>
                  <span>Send</span>
                  <i className="fa-solid fa-paper-plane" />
                </>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
