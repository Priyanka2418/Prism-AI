import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function AppNavbar() {
  const { user, candidateProfile, mentorProfile, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  const role = user?.role;
  const isMentorRole = role === "MENTOR";
  const isCandidateRole = role === "CANDIDATE";

  const displayName = isMentorRole
    ? mentorProfile?.displayName || user?.email?.split("@")[0] || "Mentor"
    : isCandidateRole
    ? candidateProfile?.displayName || user?.email?.split("@")[0] || "Candidate"
    : user?.email?.split("@")[0] || "Admin";

  const isActive = (path) => location.pathname === path;

  const candidateLinks = [
    { to: "/candidate/dashboard", icon: "fa-chart-pie", label: "Dashboard" },
    { to: "/interviews/new", icon: "fa-plus", label: "New Interview" },
    { to: "/mentors", icon: "fa-chalkboard-user", label: "Find Mentors" },
    { to: "/candidate/mentoring", icon: "fa-comments", label: "My Sessions" },
  ];

  const mentorLinks = [
    { to: "/mentor/dashboard", icon: "fa-user-tie", label: "Mentor Dashboard" },
    { to: "/mentor/profile", icon: "fa-id-card", label: "Public Profile" },
  ];

  const adminLinks = [
    { to: "/admin/dashboard", icon: "fa-shield-halved", label: "Mentor Verification" },
  ];

  const navLinks =
    role === "MENTOR" ? mentorLinks :
    role === "ADMIN" ? adminLinks :
    candidateLinks;

  return (
    <header className="sticky top-0 z-40 w-full border-b border-[#334155]/80 bg-[#0F172A]/85 backdrop-blur-xl">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center gap-8">
          <Link
            to={
              role === "ADMIN"
                ? "/admin/dashboard"
                : role === "MENTOR"
                ? "/mentor/dashboard"
                : "/candidate/dashboard"
            }
            className="flex items-center gap-2.5 group"
          >
            <div className="w-8 h-8 rounded-lg bg-[#2DD4BF] flex items-center justify-center text-[#0F172A] font-bold shadow-[0_0_15px_rgba(45,212,191,0.3)] transition-transform group-hover:scale-105">
              <i className="fa-solid fa-bolt" />
            </div>
            <span className="text-xl font-bold tracking-tight text-white">
              Prism<span className="text-[#2DD4BF]">.AI</span>
            </span>
          </Link>

          {/* Desktop Nav Links */}
          <nav className="hidden md:flex items-center gap-1">
            {navLinks.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                className={`px-3.5 py-1.5 rounded-lg text-sm font-medium transition-all ${
                  isActive(link.to)
                    ? "bg-[#1E293B] text-[#2DD4BF] border border-[#2DD4BF]/30"
                    : "text-[#94A3B8] hover:text-white hover:bg-[#1E293B]/50"
                }`}
              >
                <i className={`fa-solid ${link.icon} mr-2 text-xs`} />
                {link.label}
              </Link>
            ))}
          </nav>
        </div>

        {/* Right Section */}
        <div className="flex items-center gap-3">
          {/* User info */}
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-[#2DD4BF] flex items-center justify-center text-xs font-bold text-white shadow">
              {displayName.charAt(0).toUpperCase()}
            </div>
            <div className="hidden sm:block text-left">
              <p className="text-sm font-semibold text-white leading-none">{displayName}</p>
              <span className="text-[11px] font-bold text-[#2DD4BF] uppercase tracking-wider">
                {role}
              </span>
            </div>
          </div>

          {/* Logout */}
          <button
            onClick={handleLogout}
            title="Sign Out"
            className="p-2 rounded-lg text-[#94A3B8] hover:text-red-400 hover:bg-[#1E293B] transition-colors"
          >
            <i className="fa-solid fa-arrow-right-from-bracket" />
          </button>

          {/* Hamburger — mobile only */}
          <button
            onClick={() => setMobileOpen((v) => !v)}
            className="md:hidden p-2 rounded-lg text-[#94A3B8] hover:text-white hover:bg-[#1E293B] transition-colors"
            aria-label="Toggle navigation menu"
          >
            <i className={`fa-solid ${mobileOpen ? "fa-xmark" : "fa-bars"} text-base`} />
          </button>
        </div>
      </div>

      {/* Mobile drawer */}
      {mobileOpen && (
        <div className="md:hidden border-t border-[#334155]/80 bg-[#0F172A]/95 px-4 py-3 flex flex-col gap-1">
          {navLinks.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              onClick={() => setMobileOpen(false)}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                isActive(link.to)
                  ? "bg-[#1E293B] text-[#2DD4BF] border border-[#2DD4BF]/20"
                  : "text-[#94A3B8] hover:text-white hover:bg-[#1E293B]/60"
              }`}
            >
              <i className={`fa-solid ${link.icon} w-4 text-center text-xs`} />
              {link.label}
            </Link>
          ))}
        </div>
      )}
    </header>
  );
}
