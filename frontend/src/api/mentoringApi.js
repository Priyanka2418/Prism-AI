import api from "./client";

export async function getPublicMentors() {
  return await api.get("/mentors");
}

export async function getPublicMentor(mentorId) {
  return await api.get(`/mentors/${mentorId}`);
}

export async function requestMentorship(mentorId, payload) {
  return await api.post(`/mentors/${mentorId}/mentoring-requests`, payload);
}

export async function getCandidateMentorRequests() {
  return await api.get("/mentors/candidate-mentoring-requests");
}

export async function getMentorPendingRequests() {
  return await api.get("/mentor/mentoring-requests");
}

export async function acceptMentorRequest(requestId) {
  return await api.patch(`/mentor/mentoring-requests/${requestId}/accept`);
}

export async function rejectMentorRequest(requestId, rejectionReason) {
  return await api.patch(`/mentor/mentoring-requests/${requestId}/reject`, { rejectionReason });
}

export async function getCandidateSessions() {
  return await api.get("/mentor-sessions/candidate");
}

export async function getMentorSessions() {
  return await api.get("/mentor-sessions/mentor");
}

export async function getSession(sessionId) {
  return await api.get(`/mentor-sessions/${sessionId}`);
}

export async function deleteSession(sessionId) {
  return await api.delete(`/mentor-sessions/${sessionId}`);
}

export async function getChatHistory(sessionId) {
  return await api.get(`/mentor-sessions/${sessionId}/chat/messages`);
}

export async function sendChatMessage(sessionId, content) {
  return await api.post(`/mentor-sessions/${sessionId}/chat/messages`, { content });
}

export async function deleteChatMessage(sessionId, messageId) {
  return await api.delete(`/mentor-sessions/${sessionId}/chat/messages/${messageId}`);
}

export async function clearMyChatMessages(sessionId) {
  return await api.delete(`/mentor-sessions/${sessionId}/chat/messages/my`);
}
