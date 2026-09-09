import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { createCandidateProfile, getCandidateProfile } from "../api/profileApi";

export default function CandidateOnboarding() {
  const { user, candidateProfile, setCandidateProfile } = useAuth();
  const navigate = useNavigate();

  const [displayName, setDisplayName] = useState("");
  const [college, setCollege] = useState("");
  const [degree, setDegree] = useState("");
  const [experienceLevel, setExperienceLevel] = useState("STUDENT");
  const [preferredDomain, setPreferredDomain] = useState("SOFTWARE_ENGINEERING");
  const [targetRole, setTargetRole] = useState("SOFTWARE_ENGINEER");
  const [skills, setSkills] = useState(["Java", "Spring Boot", "React"]);
  const [skillInput, setSkillInput] = useState("");
  const [resumeUrl, setResumeUrl] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [checking, setChecking] = useState(true);

  useEffect(() => {
    async function checkExisting() {
      if (candidateProfile) {
        navigate("/candidate/dashboard", { replace: true });
        return;
      }
      try {
        const profile = await getCandidateProfile();
        if (profile?.id) {
          setCandidateProfile(profile);
          navigate("/candidate/dashboard", { replace: true });
          return;
        }
      } catch {
        // No existing profile, proceed with onboarding
      } finally {
        setChecking(false);
      }
    }
    checkExisting();
  }, [candidateProfile, navigate, setCandidateProfile]);

  const handleAddSkill = (e) => {
    if ((e.key === "Enter" || e.type === "click") && skillInput.trim()) {
      e.preventDefault();
      if (!skills.includes(skillInput.trim())) {
        setSkills([...skills, skillInput.trim()]);
      }
      setSkillInput("");
    }
  };

  const handleRemoveSkill = (skillToRemove) => {
    setSkills(skills.filter((s) => s !== skillToRemove));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!displayName.trim() || !college.trim() || !degree.trim()) {
      setError("Please fill in your name, college, and degree.");
      return;
    }
    if (skills.length === 0) {
      setError("Please add at least one technical skill.");
      return;
    }

    setError("");
    setIsSubmitting(true);

    try {
      const response = await createCandidateProfile({
        displayName: displayName.trim(),
        college: college.trim(),
        degree: degree.trim(),
        experienceLevel,
        preferredDomain,
        targetRole,
        skills,
        resumeUrl: resumeUrl.trim() || null,
      });

      setCandidateProfile(response);
      navigate("/candidate/dashboard");
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to create profile. Please check your details.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (checking) {
    return (
      <div className="min-h-screen bg-[#0F172A] flex items-center justify-center text-white">
        <div className="w-10 h-10 border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Background glow */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-[#2DD4BF]/10 rounded-full blur-[120px]" />
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-indigo-500/10 rounded-full blur-[120px]" />
      </div>

      <div className="max-w-2xl w-full mx-auto relative z-10">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#2DD4BF]/10 border border-[#2DD4BF]/20 text-[#2DD4BF] text-xs font-bold uppercase tracking-wider mb-4">
            <i className="fa-solid fa-sparkles" /> One-Time Setup
          </div>
          <h1 className="text-3xl font-extrabold text-white sm:text-4xl">
            Complete Your Candidate Profile
          </h1>
          <p className="mt-2 text-sm text-[#94A3B8]">
            Prism.AI customizes questions, difficulty, and mentoring opportunities based on your profile.
          </p>
        </div>

        {/* Card */}
        <div className="bg-[#1E293B] border border-[#334155] rounded-2xl shadow-xl p-8 backdrop-blur-md">
          {error && (
            <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-3">
              <i className="fa-solid fa-circle-exclamation text-lg" />
              <span>{error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Display Name */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Full Name / Display Name *
              </label>
              <input
                type="text"
                required
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                placeholder="e.g. Alex Johnson"
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
              />
            </div>

            {/* College & Degree */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  College / University *
                </label>
                <input
                  type="text"
                  required
                  value={college}
                  onChange={(e) => setCollege(e.target.value)}
                  placeholder="e.g. Stanford University"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Degree / Major *
                </label>
                <input
                  type="text"
                  required
                  value={degree}
                  onChange={(e) => setDegree(e.target.value)}
                  placeholder="e.g. B.S. Computer Science"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
                />
              </div>
            </div>

            {/* Experience Level & Target Role */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Experience Level *
                </label>
                <select
                  value={experienceLevel}
                  onChange={(e) => setExperienceLevel(e.target.value)}
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF]"
                >
                  <option value="STUDENT">Student</option>
                  <option value="FRESHER">Fresher (0 - 1 yr)</option>
                  <option value="JUNIOR">Junior (1 - 3 yrs)</option>
                  <option value="MID_LEVEL">Mid-Level (3 - 5 yrs)</option>
                  <option value="SENIOR">Senior (5+ yrs)</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Target Role *
                </label>
                <select
                  value={targetRole}
                  onChange={(e) => setTargetRole(e.target.value)}
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF]"
                >
                  <option value="SOFTWARE_ENGINEER">Software Engineer</option>
                  <option value="BACKEND_DEVELOPER">Backend Developer</option>
                  <option value="FRONTEND_DEVELOPER">Frontend Developer</option>
                  <option value="FULL_STACK_DEVELOPER">Full Stack Developer</option>
                  <option value="DATA_ENGINEER">Data Engineer</option>
                  <option value="DATA_SCIENTIST">Data Scientist</option>
                  <option value="ML_ENGINEER">ML Engineer</option>
                  <option value="DEVOPS_ENGINEER">DevOps Engineer</option>
                  <option value="CLOUD_ENGINEER">Cloud Engineer</option>
                  <option value="QA_ENGINEER">QA Engineer</option>
                </select>
              </div>
            </div>

            {/* Preferred Domain */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Preferred Domain *
              </label>
              <select
                value={preferredDomain}
                onChange={(e) => setPreferredDomain(e.target.value)}
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF]"
              >
                <option value="SOFTWARE_ENGINEERING">Software Engineering</option>
                <option value="DATA_SCIENCE">Data Science</option>
                <option value="MACHINE_LEARNING">Machine Learning & AI</option>
                <option value="DEVOPS">DevOps & CI/CD</option>
                <option value="CLOUD_COMPUTING">Cloud Computing</option>
                <option value="CYBER_SECURITY">Cyber Security</option>
                <option value="PRODUCT_ENGINEERING">Product Engineering</option>
                <option value="QA_TESTING">QA & Testing</option>
              </select>
            </div>

            {/* Skills */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Key Technical Skills *
              </label>
              <div className="flex gap-2 mb-3">
                <input
                  type="text"
                  value={skillInput}
                  onChange={(e) => setSkillInput(e.target.value)}
                  onKeyDown={handleAddSkill}
                  placeholder="Type a skill (e.g. Docker, Python) and press Enter"
                  className="flex-1 px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
                <button
                  type="button"
                  onClick={handleAddSkill}
                  className="px-5 py-2.5 rounded-xl bg-[#334155] text-white hover:bg-[#2DD4BF] hover:text-[#0F172A] font-medium transition-colors"
                >
                  Add
                </button>
              </div>

              <div className="flex flex-wrap gap-2 min-h-[38px] p-2 rounded-xl bg-[#0F172A]/50 border border-[#334155]/50">
                {skills.map((skill) => (
                  <span
                    key={skill}
                    className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg bg-[#2DD4BF]/10 text-[#2DD4BF] border border-[#2DD4BF]/20 text-xs font-semibold"
                  >
                    {skill}
                    <button
                      type="button"
                      onClick={() => handleRemoveSkill(skill)}
                      className="hover:text-red-400"
                    >
                      &times;
                    </button>
                  </span>
                ))}
              </div>
            </div>

            {/* Resume Link */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Resume URL (Optional)
              </label>
              <input
                type="url"
                value={resumeUrl}
                onChange={(e) => setResumeUrl(e.target.value)}
                placeholder="https://drive.google.com/... or LinkedIn"
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] focus:ring-2 focus:ring-[#2DD4BF]/10"
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full py-4 bg-[#2DD4BF] text-[#0F172A] font-bold rounded-xl shadow-[0_0_20px_rgba(45,212,191,0.3)] hover:shadow-[0_0_30px_rgba(45,212,191,0.5)] transition-all active:scale-[0.98] disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {isSubmitting ? (
                <>
                  <i className="fa-solid fa-spinner fa-spin" />
                  Saving Profile...
                </>
              ) : (
                <>
                  <span>Save & Continue to Dashboard</span>
                  <i className="fa-solid fa-arrow-right" />
                </>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
