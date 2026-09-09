import signup from "../assets/signup.png";
import { useState, useEffect } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Signup() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { signup } = useAuth();

  const initialRole = searchParams.get("role")?.toLowerCase() === "mentor" ? "mentor" : "candidate";
  const [role, setRole] = useState(initialRole);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    const urlRole = searchParams.get("role")?.toLowerCase();
    if (urlRole === "mentor" || urlRole === "candidate") {
      setRole(urlRole);
    }
  }, [searchParams]);

  const handleRoleChange = (newRole) => {
    setRole(newRole);
    setSearchParams({ role: newRole });
    setError("");
  };

  const validateForm = () => {
    if (!email.trim()) {
      return "Email address is required.";
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      return "Please enter a valid email address.";
    }

    if (!password) {
      return "Password is required.";
    }

    if (password.length < 8 || password.length > 20) {
      return "Password must be between 8 and 20 characters.";
    }

    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationError = validateForm();

    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      await signup(email.trim(), password, role);

      if (role === "candidate") {
        navigate("/candidate/onboarding");
      } else {
        navigate("/mentor/profile");
      }
    } catch (error) {
      console.error(error);
      if (error.message?.includes("already registered") || error.status === 409) {
        setError("This email is already registered. If you already have an account, please sign in.");
      } else {
        setError(error.message || "Registration failed. Please try again.");
      }
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

      {/* Main Signup Card */}
      <div className="relative z-10 w-full max-w-6xl grid grid-cols-1 lg:grid-cols-12 bg-[#1E293B] border border-[#334155] rounded-3xl overflow-hidden shadow-2xl">
        {/* LEFT SIDE */}
        <div className="hidden lg:flex lg:col-span-4 flex-col justify-between p-12 bg-[#0F172A]/50 relative overflow-hidden">
          {/* Background image */}
          <div className="absolute inset-0 opacity-20 pointer-events-none">
            <img src={signup} alt="" className="w-full h-full object-cover" />
          </div>

          {/* Content */}
          <div className="relative z-20">
            {/* Brand */}
            <div className="flex items-center gap-3 mb-12">
              <div className="w-9 h-9 rounded-lg bg-[#2DD4BF] flex items-center justify-center">
                <i className="fa-solid fa-bolt text-[#0F172A]" />
              </div>

              <span className="text-xl font-bold tracking-tight">Prism.AI</span>
            </div>

            {/* Benefits */}
            <div className="space-y-8">
              {/* Benefit 1 */}
              <div className="flex gap-4">
                <div className="w-10 h-10 shrink-0 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 flex items-center justify-center">
                  <i className="fa-solid fa-check text-[#2DD4BF] text-sm" />
                </div>

                <div>
                  <h4 className="font-bold mb-1">AI Mock Interviews</h4>

                  <p className="text-sm text-[#94A3B8]">
                    Practice anytime with realistic AI-powered interviews.
                  </p>
                </div>
              </div>

              {/* Benefit 2 */}
              <div className="flex gap-4">
                <div className="w-10 h-10 shrink-0 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 flex items-center justify-center">
                  <i className="fa-solid fa-check text-[#2DD4BF] text-sm" />
                </div>

                <div>
                  <h4 className="font-bold mb-1">Instant Feedback</h4>

                  <p className="text-sm text-[#94A3B8]">
                    Understand your performance and identify areas to improve.
                  </p>
                </div>
              </div>

              {/* Benefit 3 */}
              <div className="flex gap-4">
                <div className="w-10 h-10 shrink-0 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 flex items-center justify-center">
                  <i className="fa-solid fa-check text-[#2DD4BF] text-sm" />
                </div>

                <div>
                  <h4 className="font-bold mb-1">Expert Mentorship</h4>

                  <p className="text-sm text-[#94A3B8]">
                    Connect with experienced mentors for personalized guidance.
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Bottom Quote */}
          <div className="relative z-20 p-6 rounded-2xl bg-[#1E293B]/80 border border-[#334155] backdrop-blur-sm">
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-full bg-[#2DD4BF]/10 flex items-center justify-center">
                <i className="fa-solid fa-user text-[#2DD4BF]" />
              </div>

              <div>
                <p className="text-sm font-bold">
                  Your next opportunity starts here.
                </p>

                <p className="text-xs text-[#94A3B8]">
                  Practice. Improve. Succeed.
                </p>
              </div>
            </div>

            <p className="text-sm italic text-slate-300">
              "The best way to prepare for an interview is to practice before
              the real one."
            </p>
          </div>
        </div>

        {/* RIGHT SIDE */}
        <div className="lg:col-span-8 p-8 md:p-16">
          <div className="max-w-2xl mx-auto">
            {/* Header */}
            <div className="flex justify-between items-start mb-10">
              <div>
                <h1 className="text-3xl font-bold mb-2">Create Account</h1>

                <p className="text-[#94A3B8]">
                  Start your interview preparation journey.
                </p>
              </div>

              <Link
                to={`/login?role=${role}`}
                className="hidden sm:block text-sm text-[#94A3B8] hover:text-[#2DD4BF] transition-colors"
              >
                Sign In
              </Link>
            </div>

            {/* Role Selection */}
            <div className="mb-10">
              <p className="text-sm font-bold uppercase tracking-widest text-[#94A3B8] mb-4">
                I want to join as a:
              </p>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {/* Candidate */}
                <button
                  type="button"
                  onClick={() => handleRoleChange("candidate")}
                  className={`text-left p-6 rounded-2xl border transition-all ${
                    role === "candidate"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/5"
                      : "border-[#334155] bg-[#0F172A] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-12 h-12 rounded-xl bg-[#1E293B] flex items-center justify-center">
                      <i className="fa-solid fa-user-graduate text-[#2DD4BF] text-xl" />
                    </div>

                    <div
                      className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                        role === "candidate"
                          ? "border-[#2DD4BF]"
                          : "border-[#334155]"
                      }`}
                    >
                      {role === "candidate" && (
                        <div className="w-2.5 h-2.5 rounded-full bg-[#2DD4BF]" />
                      )}
                    </div>
                  </div>

                  <h2 className="font-bold mb-1">Candidate</h2>

                  <p className="text-xs text-[#94A3B8]">
                    Practice interviews and improve your performance with AI.
                  </p>
                </button>

                {/* Mentor */}
                <button
                  type="button"
                  onClick={() => handleRoleChange("mentor")}
                  className={`text-left p-6 rounded-2xl border transition-all ${
                    role === "mentor"
                      ? "border-[#2DD4BF] bg-[#2DD4BF]/5"
                      : "border-[#334155] bg-[#0F172A] hover:border-[#2DD4BF]/40"
                  }`}
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className="w-12 h-12 rounded-xl bg-[#1E293B] flex items-center justify-center">
                      <i className="fa-solid fa-chalkboard-user text-[#2DD4BF] text-xl" />
                    </div>

                    <div
                      className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                        role === "mentor"
                          ? "border-[#2DD4BF]"
                          : "border-[#334155]"
                      }`}
                    >
                      {role === "mentor" && (
                        <div className="w-2.5 h-2.5 rounded-full bg-[#2DD4BF]" />
                      )}
                    </div>
                  </div>

                  <h2 className="font-bold mb-1">Mentor</h2>

                  <p className="text-xs text-[#94A3B8]">
                    Review interviews and provide expert guidance.
                  </p>
                </button>
              </div>
            </div>

            {/* Signup Form */}
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

                <p className="mt-2 text-[11px] text-[#94A3B8] flex items-center gap-1">
                  <i className="fa-solid fa-circle-info text-[8px]" />
                  Password must be between 8 and 20 characters.
                </p>
              </div>

              {/* Error */}
              {error && <p className="text-sm text-red-400">{error}</p>}

              {/* Submit */}
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full py-4 bg-[#2DD4BF] text-[#0F172A] font-bold rounded-xl hover:shadow-[0_0_20px_rgba(45,212,191,0.35)] transition-all active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isSubmitting
                  ? "Creating Account..."
                  : role === "mentor"
                  ? "Register as Mentor"
                  : "Register as Candidate"}
              </button>
            </form>

            {/* Mobile Sign In */}
            <div className="mt-8 sm:hidden text-center">
              <p className="text-sm text-[#94A3B8]">
                Already have an account?{" "}
                <Link to={`/login?role=${role}`} className="text-[#2DD4BF] font-bold ml-1">
                  Sign In
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Signup;
