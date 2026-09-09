import api from "./client";

export async function getAllMentors() {
  return await api.get("/admin/mentors");
}

export async function getPendingMentors() {
  return await api.get("/admin/mentors/pending");
}

export async function getMentorForVerification(mentorProfileId) {
  return await api.get(`/admin/mentors/${mentorProfileId}`);
}

export async function verifyMentor(mentorProfileId) {
  return await api.patch(`/admin/mentors/${mentorProfileId}/verify`);
}

export async function rejectMentor(mentorProfileId, rejectionReason) {
  return await api.patch(`/admin/mentors/${mentorProfileId}/reject`, { rejectionReason });
}

export async function getAllUsers() {
  return await api.get("/users");
}

export async function deleteUser(userId) {
  return await api.delete(`/users/${userId}`);
}
