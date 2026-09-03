import { useState } from "react";
import { Link } from "react-router-dom";
import login from "../assets/login.png";
import { loginUser } from "../api/authApi";

function Login() {
  const [role, setRole] = useState("candidate");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

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
      await loginUser(email.trim(), password);

      console.log("Login successful");
    } catch (error) {
      console.error(error);
      setError(error.message || "Something went wrong. Please try again.");
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
        {/* LEFT SIDE */}
        <div className="hidden md:flex flex-col justify-between p-12 bg-[#0F172A]/50 relative overflow-hidden">
          {/* Background image */}
          <div className="absolute inset-0 opacity-20 pointer-events-none">
            <img src={login} alt="" className="w-full h-full object-cover" />
          </div>

          {/* Left content */}
          <div className="relative z-20">
            {/* Brand */}
            <div className="flex items-center gap-3 mb-12">
              <div className="w-9 h-9 rounded-lg bg-[#2DD4BF] flex items-center justify-center">
                <i className="fa-solid fa-bolt text-[#0F172A]" />
              </div>

              <span className="text-xl font-bold tracking-tight">Prism.AI</span>
            </div>

            {/* Heading */}
            <h1 className="text-4xl font-bold leading-tight mb-6">
              Welcome back to the future of{" "}
              <span className="text-[#2DD4BF]">interviewing.</span>
            </h1>

            <p className="text-[#94A3B8] text-lg leading-relaxed">
              Access your personalized dashboard, track your progress, and
              prepare with AI-powered precision.
            </p>
          </div>

          {/* Bottom text */}
          <div className="relative z-20">
            <p className="text-sm text-[#94A3B8]">
              Practice smarter. Interview better.
            </p>
          </div>
        </div>

        {/* RIGHT SIDE */}
        <div className="p-8 md:p-12 lg:p-16 flex flex-col justify-center">
          <div className="max-w-md w-full mx-auto">
            {/* Header */}
            <div className="mb-10">
              <h2 className="text-3xl font-bold mb-2">Welcome Back</h2>

              <p className="text-[#94A3B8]">
                Sign in to continue your interview journey.
              </p>
            </div>

            {/* Role Selection */}
            <div className="mb-8">
              <p className="text-sm font-bold uppercase tracking-widest text-[#94A3B8] mb-4">
                Sign in as:
              </p>

              <div className="grid grid-cols-2 gap-3">
                {/* Candidate */}
                <button
                  type="button"
                  onClick={() => setRole("candidate")}
                  className={`py-3 px-4 rounded-xl border font-medium transition-all ${
                    role === "candidate"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                      : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <i className="fa-solid fa-user-graduate mr-2" />
                  Candidate
                </button>

                {/* Mentor */}
                <button
                  type="button"
                  onClick={() => setRole("mentor")}
                  className={`py-3 px-4 rounded-xl border font-medium transition-all ${
                    role === "mentor"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/10 text-[#2DD4BF]"
                      : "border-[#334155] bg-[#0F172A] text-[#94A3B8] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <i className="fa-solid fa-chalkboard-user mr-2" />
                  Mentor
                </button>
              </div>
            </div>

            {/* Login Form */}
            <form className="space-y-6" onSubmit={handleSubmit}>
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
                  placeholder="you@example.com"
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
              {error && <p className="text-sm text-red-400">{error}</p>}

              {/* Submit */}
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full py-4 bg-[#2DD4BF] text-[#0F172A] font-bold rounded-xl hover:shadow-[0_0_20px_rgba(45,212,191,0.35)] transition-all active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {isSubmitting ? "Signing In..." : "Sign In"}
              </button>
            </form>

            {/* Signup */}
            <p className="mt-8 text-center text-sm text-[#94A3B8]">
              Don't have an account?{" "}
              <Link
                to="/signup"
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
