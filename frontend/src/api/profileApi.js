import api from "./client";

// Candidate Profile
export async function createCandidateProfile(profileData) {
  return await api.post("/student-profiles", profileData);
}

export async function getCandidateProfile() {
  return await api.get("/student-profiles/me");
}

export async function updateCandidateProfile(profileData) {
  return await api.patch("/student-profiles/me", profileData);
}

// Mentor Profile
export async function createMentorProfile(profileData) {
  return await api.post("/mentor-profiles", profileData);
}

export async function getMentorProfile() {
  return await api.get("/mentor-profiles/me");
}

export async function updateMentorPublicProfile(profileData) {
  return await api.put("/mentor-profiles/me/public-profile", profileData);
}

export async function getMentorPublicProfile() {
  return await api.get("/mentor-profiles/me/public-profile");
}
