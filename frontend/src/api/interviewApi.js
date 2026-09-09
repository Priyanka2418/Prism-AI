import api from "./client";

export async function createInterview(payload) {
  return await api.post("/interviews", payload);
}

export async function getMyInterviews() {
  return await api.get("/interviews");
}

export async function getInterview(interviewId) {
  return await api.get(`/interviews/${interviewId}`);
}

export async function startInterview(interviewId) {
  return await api.post(`/interviews/${interviewId}/start`, {});
}

export async function completeInterview(interviewId) {
  return await api.post(`/interviews/${interviewId}/complete`, {});
}

export async function cancelInterview(interviewId) {
  return await api.post(`/interviews/${interviewId}/cancel`, {});
}

export async function deleteInterview(interviewId) {
  return await api.delete(`/interviews/${interviewId}`);
}

export async function startFirstTurn(interviewId) {
  return await api.post(`/interviews/${interviewId}/turns/start`, {});
}

export async function submitAnswer(interviewId, questionTurnId, { content, answerDurationSeconds = 30 }) {
  return await api.post(`/interviews/${interviewId}/turns/${questionTurnId}/answer`, {
    content,
    answerDurationSeconds,
  });
}

export async function getInterviewTurns(interviewId) {
  return await api.get(`/interviews/${interviewId}/turns`);
}

export async function generateFeedback(interviewId) {
  return await api.post(`/interviews/${interviewId}/feedback/generate`, {});
}

export async function getFeedback(interviewId) {
  return await api.get(`/interviews/${interviewId}/feedback`);
}
