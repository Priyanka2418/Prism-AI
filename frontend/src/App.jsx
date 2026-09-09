import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/common/ProtectedRoute";

import Navbar from "./components/landing/Navbar";
import Hero from "./components/landing/Hero";
import Marquee from "./components/landing/Marquee";
import Features from "./components/landing/Features";
import Process from "./components/landing/Process";
import FAQ from "./components/landing/FAQ";
import Footer from "./components/landing/Footer";

// Pages
import Signup from "./pages/Signup";
import Login from "./pages/Login";
import CandidateOnboarding from "./pages/CandidateOnboarding";
import CandidateDashboard from "./pages/CandidateDashboard";
import CreateInterview from "./pages/CreateInterview";
import InterviewRoom from "./pages/InterviewRoom";
import InterviewFeedback from "./pages/InterviewFeedback";
import MentorDiscovery from "./pages/MentorDiscovery";
import CandidateMentoring from "./pages/CandidateMentoring";
import SessionChat from "./pages/SessionChat";
import MentorProfile from "./pages/MentorProfile";
import MentorDashboard from "./pages/MentorDashboard";
import AdminDashboard from "./pages/AdminDashboard";

function LandingPage() {
  return (
    <div className="scroll-smooth bg-[#05040A] text-white">
      <Navbar />
      <Hero />
      <Marquee />
      <Features />
      <Process />
      <FAQ />
      <Footer />
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/login" element={<Login />} />

          {/* Candidate Routes */}
          <Route
            path="/candidate/onboarding"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE"]}>
                <CandidateOnboarding />
              </ProtectedRoute>
            }
          />
          <Route
            path="/candidate/dashboard"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE"]}>
                <CandidateDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/interviews/new"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR", "ADMIN"]}>
                <CreateInterview />
              </ProtectedRoute>
            }
          />
          <Route
            path="/interviews/:id/room"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR", "ADMIN"]}>
                <InterviewRoom />
              </ProtectedRoute>
            }
          />
          <Route
            path="/interviews/:id/feedback"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR", "ADMIN"]}>
                <InterviewFeedback />
              </ProtectedRoute>
            }
          />
          <Route
            path="/mentors"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR", "ADMIN"]}>
                <MentorDiscovery />
              </ProtectedRoute>
            }
          />
          <Route
            path="/candidate/mentoring"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR", "ADMIN"]}>
                <CandidateMentoring />
              </ProtectedRoute>
            }
          />

          {/* Shared Real-Time Session Chat */}
          <Route
            path="/sessions/:sessionId"
            element={
              <ProtectedRoute allowedRoles={["CANDIDATE", "MENTOR"]}>
                <SessionChat />
              </ProtectedRoute>
            }
          />

          {/* Mentor Routes */}
          <Route
            path="/mentor/profile"
            element={
              <ProtectedRoute allowedRoles={["MENTOR"]}>
                <MentorProfile />
              </ProtectedRoute>
            }
          />
          <Route
            path="/mentor/dashboard"
            element={
              <ProtectedRoute allowedRoles={["MENTOR"]}>
                <MentorDashboard />
              </ProtectedRoute>
            }
          />

          {/* Admin Routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
