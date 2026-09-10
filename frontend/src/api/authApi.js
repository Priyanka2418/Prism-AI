import api from "./client";

export async function loginUser(email, password) {
  const data = await api.post("/auth/login", { email, password });
  if (data?.accessToken) {
    sessionStorage.setItem("prism_access_token", data.accessToken);
    localStorage.setItem("prism_access_token", data.accessToken);
  }
  return data;
}

export async function registerCandidate(email, password) {
  const data = await api.post("/auth/register/candidate", { email, password });
  if (data?.accessToken) {
    sessionStorage.setItem("prism_access_token", data.accessToken);
    localStorage.setItem("prism_access_token", data.accessToken);
  }
  return data;
}

export async function registerMentor(email, password) {
  const data = await api.post("/auth/register/mentor", { email, password });
  if (data?.accessToken) {
    sessionStorage.setItem("prism_access_token", data.accessToken);
    localStorage.setItem("prism_access_token", data.accessToken);
  }
  return data;
}

export async function getCurrentUser() {
  return await api.get("/users/me");
}

export async function refreshTokens() {
  const data = await api.post("/auth/refresh", {});
  if (data?.accessToken) {
    sessionStorage.setItem("prism_access_token", data.accessToken);
    localStorage.setItem("prism_access_token", data.accessToken);
  }
  return data;
}

export async function logoutUser() {
  try {
    await api.post("/auth/logout", {});
  } finally {
    sessionStorage.removeItem("prism_access_token");
    sessionStorage.removeItem("prism_user");
    localStorage.removeItem("prism_access_token");
    localStorage.removeItem("prism_user");
  }
}
