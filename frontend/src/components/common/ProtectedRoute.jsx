import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function ProtectedRoute({ children, allowedRoles }) {
  const { user, isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] flex flex-col items-center justify-center text-white">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <p className="text-[#94A3B8] font-medium tracking-wide">Initializing session...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    if (user?.role === "ADMIN") return <Navigate to="/admin/dashboard" replace />;
    if (user?.role === "MENTOR") return <Navigate to="/mentor/dashboard" replace />;
    return <Navigate to="/candidate/dashboard" replace />;
  }

  return children;
}
