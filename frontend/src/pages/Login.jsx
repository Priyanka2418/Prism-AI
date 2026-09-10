import { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { login, logout } = useAuth();

  const initialRole = searchParams.get("role")?.toLowerCase() === "mentor"
    ? "mentor"
    : searchParams.get("role")?.toLowerCase() === "admin"
    ? "admin"
    : "candidate";
  const [role, setRole] = useState(initialRole);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const urlRole = searchParams.get("role")?.toLowerCase();
    if (urlRole === "mentor" || urlRole === "candidate" || urlRole === "admin") {
      setRole(urlRole);
    }
  }, [searchParams]);

  const handleRoleChange = (newRole) => {
    setRole(newRole);
    setSearchParams({ role: newRole });
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!email.trim()) {
      setError("Please enter your email address.");
      return;
    }

    if (!password) {
      setError("Please enter your password.");
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      const { user } = await login(email.trim(), password);

      const expectedRole = role.toUpperCase(); // "CANDIDATE", "MENTOR", or "ADMIN"
      const actualRole = user?.role;

      // Strict role enforcement: user must login through their assigned role tab
      if (actualRole !== expectedRole) {
        await logout();

        if (actualRole === "ADMIN") {
          setError("This account is registered as an Administrator. Please select the 'Admin' tab above to sign in.");
        } else if (actualRole === "MENTOR") {
          setError("This account is registered as a Mentor. Please select the 'Mentor' tab above to sign in.");
        } else if (actualRole === "CANDIDATE") {
          setError("This account is registered as a Candidate. Please select the 'Candidate' tab above to sign in.");
        } else {
          setError("Invalid role for this account. Please sign in with your designated role.");
        }
        return;
      }

      if (actualRole === "ADMIN") {
        navigate("/admin/dashboard");
      } else if (actualRole === "MENTOR") {
        navigate("/mentor/dashboard");
      } else {
        navigate("/candidate/dashboard");
      }
    } catch (error) {
      console.error(error);
      setError(error.message || "Invalid email or password. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex items-center justify-center p-6 relative overflow-hidden">
      {/* Background glow */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-[#2DD4BF]/10 rounded-full blur-[120px]" />

        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-indigo-500/10 rounded-full blur-[120px]" />
      </div>

      {/* Main Login Card */}
      <div className="relative z-10 w-full max-w-5xl grid grid-cols-1 md:grid-cols-2 bg-[#1E293B] border border-[#334155] rounded-3xl overflow-hidden shadow-2xl">
        {/* LEFT SIDE (Image-free sleek branding) */}
        <div className="hidden md:flex flex-col justify-between p-10 lg:p-12 bg-[#0F172A]/80 border-r border-[#334155] relative overflow-hidden">
          {/* Top content */}
          <div>
            {/* Brand */}
            <div className="flex items-center gap-3 mb-8">
              <div className="w-9 h-9 rounded-lg bg-[#2DD4BF] flex items-center justify-center">
                <i className="fa-solid fa-bolt text-[#0F172A]" />
              </div>

              <span className="text-xl font-bold tracking-tight">Prism.AI</span>
            </div>

            {/* Heading */}
            <h1 className="text-3xl lg:text-4xl font-bold leading-tight mb-4">
              Welcome back to the future of{" "}
              <span className="text-[#2DD4BF]">interviewing.</span>
            </h1>

            <p className="text-[#94A3B8] text-sm lg:text-base leading-relaxed mb-8">
              Access your personalized dashboard, track your progress, and
              prepare with AI-powered precision.
            </p>

            {/* Feature highlights */}
            <div className="space-y-4">
              <div className="p-4 rounded-2xl bg-[#1E293B]/70 border border-[#334155] flex items-center gap-3.5">
                <div className="w-9 h-9 rounded-xl bg-[#2DD4BF]/10 text-[#2DD4BF] flex items-center justify-center shrink-0">
                  <i className="fa-solid fa-microchip text-sm" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">AI-Powered Simulations</h4>
                  <p className="text-[11px] text-[#94A3B8]">Realistic interview turns tailored to your exact tech stack.</p>
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-[#1E293B]/70 border border-[#334155] flex items-center gap-3.5">
                <div className="w-9 h-9 rounded-xl bg-indigo-500/10 text-indigo-400 flex items-center justify-center shrink-0">
                  <i className="fa-solid fa-chart-line text-sm" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">In-Depth Feedback Reports</h4>
                  <p className="text-[11px] text-[#94A3B8]">Turn-by-turn analysis of answers, strengths, and areas to grow.</p>
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-[#1E293B]/70 border border-[#334155] flex items-center gap-3.5">
                <div className="w-9 h-9 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center shrink-0">
                  <i className="fa-solid fa-handshake-angle text-sm" />
                </div>
                <div>
                  <h4 className="text-xs font-bold text-white">Verified Mentor Network</h4>
                  <p className="text-[11px] text-[#94A3B8]">Connect 1-on-1 with industry leaders for mock reviews and guidance.</p>
                </div>
              </div>
            </div>
          </div>

          {/* Bottom text */}
          <div className="pt-6 border-t border-[#334155]/60 mt-8">
            <p className="text-xs text-[#94A3B8]">
              Practice smarter. Interview better. Powered by Prism.AI.
            </p>
          </div>
        </div>

        {/* RIGHT SIDE */}
        <div className="p-8 md:p-12 lg:p-16 flex flex-col justify-center">
          <div className="max-w-md w-full mx-auto">
            {/* Header */}
            <div className="mb-8">
              <h2 className="text-3xl font-bold mb-2">Welcome Back</h2>

              <p className="text-[#94A3B8]">
                Sign in to continue your interview journey.
              </p>
            </div>

            {/* Role Selection */}
            <div className="mb-8">
              <p className="text-xs font-bold uppercase tracking-widest text-[#94A3B8] mb-3">
                Sign in as:
              </p>

              <div className="grid grid-cols-3 gap-2">
                {/* Candidate */}
                <button
                  type="button"
                  onClick={() => handleRoleChange("candidate")}
                  className={`py-3 px-2 rounded-xl border font-medium text-xs transition-all flex items-center justify-center gap-1.5 ${
                    role === "candidate"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                      : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <i className="fa-solid fa-user-graduate text-xs" />
                  Candidate
                </button>

                {/* Mentor */}
                <button
                  type="button"
                  onClick={() => handleRoleChange("mentor")}
                  className={`py-3 px-2 rounded-xl border font-medium text-xs transition-all flex items-center justify-center gap-1.5 ${
                    role === "mentor"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                      : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <i className="fa-solid fa-chalkboard-user text-xs" />
                  Mentor
                </button>

                {/* Admin */}
                <button
                  type="button"
                  onClick={() => handleRoleChange("admin")}
                  className={`py-3 px-2 rounded-xl border font-medium text-xs transition-all flex items-center justify-center gap-1.5 ${
                    role === "admin"
                      ? "border-rose-400 bg-rose-500/15 text-rose-300 shadow-[0_0_12px_rgba(244,63,94,0.25)]"
                      : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-rose-400/40"
                  }`}
                >
                  <i className="fa-solid fa-shield-halved text-xs text-rose-400" />
                  Admin
                </button>
              </div>
            </div>

            {/* Login Form */}
            <form className="space-y-5" onSubmit={handleSubmit}>
              {/* Email */}
              <div>
                <label
                  htmlFor="email"
                  className="block text-sm font-medium text-[#94A3B8] mb-2"
                >
                  Email Address
                </label>

                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder={role === "admin" ? "admin@aimock.com" : "you@example.com"}
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/60 outline-none transition-all focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
                />
              </div>

              {/* Password */}
              <div>
                <label
                  htmlFor="password"
                  className="block text-sm font-medium text-[#94A3B8] mb-2"
                >
                  Password
                </label>

                <div className="relative">
                  <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    className="w-full px-4 py-3 pr-12 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/60 outline-none transition-all focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
                  />

                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-4 top-1/2 -translate-y-1/2 text-[#94A3B8] hover:text-[#2DD4BF] transition-colors"
                    aria-label={
                      showPassword ? "Hide password" : "Show password"
                    }
                  >
                    <i
                      className={`fa-solid ${
                        showPassword ? "fa-eye-slash" : "fa-eye"
                      }`}
                    />
                  </button>
                </div>
              </div>

              {/* Error message */}
              {error && (
                <div className="p-3 rounded-xl bg-red-500/10 border border-red-500/30 text-red-400 text-xs">
                  {error}
                </div>
              )}

              {/* Submit */}
              <button
                type="submit"
                disabled={isSubmitting}
                className={`w-full py-4 font-bold rounded-xl transition-all active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed flex items-center justify-center gap-2 ${
                  role === "admin"
                    ? "bg-rose-500 text-white hover:bg-rose-600 hover:shadow-[0_0_20px_rgba(244,63,94,0.4)]"
                    : "bg-[#2DD4BF] text-[#0F172A] hover:shadow-[0_0_20px_rgba(45,212,191,0.35)]"
                }`}
              >
                {isSubmitting ? (
                  <>
                    <i className="fa-solid fa-spinner fa-spin" />
                    <span>Signing In...</span>
                  </>
                ) : role === "admin" ? (
                  <>
                    <i className="fa-solid fa-shield-halved" />
                    <span>Sign In as Admin</span>
                  </>
                ) : role === "mentor" ? (
                  <>
                    <i className="fa-solid fa-chalkboard-user" />
                    <span>Sign In as Mentor</span>
                  </>
                ) : (
                  <>
                    <i className="fa-solid fa-user-graduate" />
                    <span>Sign In as Candidate</span>
                  </>
                )}
              </button>
            </form>

            {/* Signup */}
            <p className="mt-8 text-center text-sm text-[#94A3B8]">
              Don't have an account?{" "}
              <Link
                to={`/signup?role=${role === "admin" ? "candidate" : role}`}
                className="text-[#2DD4BF] font-bold hover:underline ml-1"
              >
                Create Account
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
