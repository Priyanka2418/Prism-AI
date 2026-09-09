import { createContext, useContext, useState, useEffect, useCallback } from "react";
import { getCurrentUser, loginUser, registerCandidate, registerMentor, logoutUser } from "../api/authApi";
import { getCandidateProfile, getMentorProfile } from "../api/profileApi";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored =
        sessionStorage.getItem("prism_user") ||
        localStorage.getItem("prism_user");
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });
  const [candidateProfile, setCandidateProfile] = useState(null);
  const [mentorProfile, setMentorProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchProfile = useCallback(async (role) => {
    if (role === "CANDIDATE") {
      try {
        const profile = await getCandidateProfile();
        setCandidateProfile(profile);
      } catch {
        setCandidateProfile(null);
      }
    } else if (role === "MENTOR") {
      try {
        const profile = await getMentorProfile();
        setMentorProfile(profile);
      } catch {
        setMentorProfile(null);
      }
    }
  }, []);

  const refreshUser = useCallback(async () => {
    try {
      const currentUser = await getCurrentUser();
      setUser(currentUser);
      sessionStorage.setItem("prism_user", JSON.stringify(currentUser));
      localStorage.setItem("prism_user", JSON.stringify(currentUser));
      if (currentUser?.role) {
        await fetchProfile(currentUser.role);
      }
      return currentUser;
    } catch {
      // Not authenticated or session expired
      setUser(null);
      setCandidateProfile(null);
      setMentorProfile(null);
      sessionStorage.removeItem("prism_user");
      sessionStorage.removeItem("prism_access_token");
      localStorage.removeItem("prism_user");
      localStorage.removeItem("prism_access_token");
      return null;
    }
  }, [fetchProfile]);

  useEffect(() => {
    const initAuth = async () => {
      setLoading(true);
      await refreshUser();
      setLoading(false);
    };
    initAuth();
  }, [refreshUser]);

  const login = async (email, password) => {
    const authData = await loginUser(email, password);
    const currentUser = await refreshUser();
    return { authData, user: currentUser };
  };

  const signup = async (email, password, role) => {
    let authData;
    if (role.toLowerCase() === "mentor") {
      authData = await registerMentor(email, password);
    } else {
      authData = await registerCandidate(email, password);
    }
    const currentUser = await refreshUser();
    return { authData, user: currentUser };
  };

  const logout = async () => {
    await logoutUser();
    setUser(null);
    setCandidateProfile(null);
    setMentorProfile(null);
  };

  const value = {
    user,
    role: user?.role,
    candidateProfile,
    mentorProfile,
    isAuthenticated: !!user,
    loading,
    login,
    signup,
    logout,
    refreshUser,
    setCandidateProfile,
    setMentorProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
