import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AppNavbar from "../components/common/AppNavbar";
import { useAuth } from "../context/AuthContext";
import {
  createMentorProfile,
  getMentorProfile,
  updateMentorPublicProfile,
  getMentorPublicProfile,
} from "../api/profileApi";

export default function MentorProfile() {
  const { mentorProfile, setMentorProfile } = useAuth();
  const navigate = useNavigate();

  const [hasProfile, setHasProfile] = useState(false);
  const [displayName, setDisplayName] = useState("");
  const [company, setCompany] = useState("");
  const [jobTitle, setJobTitle] = useState("");
  const [yearsOfExperience, setYearsOfExperience] = useState("5.0");
  const [linkedinUrl, setLinkedinUrl] = useState("");

  // Public Profile Extra Fields
  const [headline, setHeadline] = useState("");
  const [bio, setBio] = useState("");
  const [expertise, setExpertise] = useState(["System Design", "Java", "Backend Architecture"]);
  const [expertiseInput, setExpertiseInput] = useState("");
  const [profileImageUrl, setProfileImageUrl] = useState("");

  const [verificationStatus, setVerificationStatus] = useState("PENDING");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadData() {
      try {
        const profile = await getMentorProfile();
        if (profile?.id) {
          setHasProfile(true);
          setDisplayName(profile.displayName || "");
          setCompany(profile.company || "");
          setJobTitle(profile.jobTitle || "");
          setYearsOfExperience(String(profile.yearsOfExperience || "5.0"));
          setLinkedinUrl(profile.linkedinUrl || "");
          setVerificationStatus(profile.verificationStatus || "PENDING");
          setMentorProfile(profile);

          try {
            const publicData = await getMentorPublicProfile();
            if (publicData) {
              setHeadline(publicData.headline || "");
              setBio(publicData.bio || "");
              if (publicData.expertise?.length > 0) {
                setExpertise(publicData.expertise);
              }
              setProfileImageUrl(publicData.profileImageUrl || "");
            }
          } catch {
            // public profile not configured yet
          }
        }
      } catch {
        setHasProfile(false);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [setMentorProfile]);

  const handleAddExpertise = (e) => {
    if ((e.key === "Enter" || e.type === "click") && expertiseInput.trim()) {
      e.preventDefault();
      if (!expertise.includes(expertiseInput.trim())) {
        setExpertise([...expertise, expertiseInput.trim()]);
      }
      setExpertiseInput("");
    }
  };

  const handleRemoveExpertise = (skill) => {
    setExpertise(expertise.filter((s) => s !== skill));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    setSaving(true);

    try {
      if (!hasProfile) {
        // Create initial profile
        const newProfile = await createMentorProfile({
          displayName: displayName.trim(),
          company: company.trim(),
          jobTitle: jobTitle.trim(),
          yearsOfExperience: parseFloat(yearsOfExperience),
          linkedinUrl: linkedinUrl.trim(),
        });
        setMentorProfile(newProfile);
        setHasProfile(true);
      }

      // Update public profile
      const publicResponse = await updateMentorPublicProfile({
        displayName: displayName.trim(),
        company: company.trim(),
        jobTitle: jobTitle.trim(),
        yearsOfExperience: parseFloat(yearsOfExperience),
        linkedinUrl: linkedinUrl.trim(),
        headline: headline.trim() || `Senior Engineer at ${company.trim()}`,
        bio: bio.trim() || "Passionate about helping candidates excel in technical interviews.",
        expertise,
        profileImageUrl: profileImageUrl.trim() || null,
      });

      setMessage("Mentor profile saved successfully!");
      setTimeout(() => navigate("/mentor/dashboard"), 1200);
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to save mentor profile.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0F172A] text-white flex flex-col items-center justify-center">
        <div className="w-12 h-12 rounded-full border-4 border-[#2DD4BF]/20 border-t-[#2DD4BF] animate-spin mb-4" />
        <p className="text-sm text-[#94A3B8]">Loading mentor profile...</p>
      </div>
    );
  }

  const statusBadge = {
    PENDING: "bg-amber-500/10 text-amber-400 border-amber-500/20",
    VERIFIED: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20",
    REJECTED: "bg-rose-500/10 text-rose-400 border-rose-500/20",
  };

  return (
    <div className="min-h-screen bg-[#0F172A] text-white flex flex-col">
      <AppNavbar />

      <main className="flex-1 max-w-3xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="mb-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 text-xs font-bold uppercase tracking-wider mb-2">
              <i className="fa-solid fa-user-shield" /> Mentor Portal
            </div>
            <h1 className="text-3xl font-black tracking-tight text-white">
              {hasProfile ? "Manage Mentor Profile" : "Setup Mentor Profile"}
            </h1>
            <p className="text-sm text-[#94A3B8] mt-1">
              Provide your professional background so administrators can verify your profile and candidates can request guidance.
            </p>
          </div>

          {hasProfile && (
            <span
              className={`text-xs font-bold uppercase tracking-wider px-3.5 py-1.5 rounded-full border self-start sm:self-center ${
                statusBadge[verificationStatus] || statusBadge.PENDING
              }`}
            >
              Status: {verificationStatus}
            </span>
          )}
        </div>

        {message && (
          <div className="mb-6 p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm flex items-center gap-2">
            <i className="fa-solid fa-circle-check" />
            <span>{message}</span>
          </div>
        )}

        {error && (
          <div className="mb-6 p-4 rounded-xl bg-red-500/10 border border-red-500/20 text-red-400 text-sm flex items-center gap-2">
            <i className="fa-solid fa-circle-exclamation" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl space-y-6">
            <h2 className="text-base font-bold text-white flex items-center gap-2 pb-4 border-b border-[#334155]">
              <i className="fa-solid fa-briefcase text-[#2DD4BF]" />
              Professional Credentials
            </h2>

            {/* Display Name */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Display Name *
              </label>
              <input
                type="text"
                required
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                placeholder="e.g. Sarah Connor"
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
              />
            </div>

            {/* Company & Job Title */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Company / Organization *
                </label>
                <input
                  type="text"
                  required
                  value={company}
                  onChange={(e) => setCompany(e.target.value)}
                  placeholder="e.g. Google, Amazon, Meta"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Job Title *
                </label>
                <input
                  type="text"
                  required
                  value={jobTitle}
                  onChange={(e) => setJobTitle(e.target.value)}
                  placeholder="e.g. Principal Software Engineer"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
              </div>
            </div>

            {/* Years of Experience & LinkedIn */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  Years of Experience *
                </label>
                <input
                  type="number"
                  step="0.5"
                  min="0"
                  max="50"
                  required
                  value={yearsOfExperience}
                  onChange={(e) => setYearsOfExperience(e.target.value)}
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white outline-none focus:border-[#2DD4BF]"
                />
              </div>

              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                  LinkedIn Profile URL *
                </label>
                <input
                  type="url"
                  required
                  value={linkedinUrl}
                  onChange={(e) => setLinkedinUrl(e.target.value)}
                  placeholder="https://linkedin.com/in/username"
                  className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
              </div>
            </div>
          </div>

          {/* Public Profile Customization Card */}
          <div className="p-6 sm:p-8 rounded-3xl bg-[#1E293B] border border-[#334155] shadow-xl space-y-6">
            <h2 className="text-base font-bold text-white flex items-center gap-2 pb-4 border-b border-[#334155]">
              <i className="fa-solid fa-globe text-[#2DD4BF]" />
              Public Mentorship Profile
            </h2>

            {/* Headline */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Headline / One-Liner
              </label>
              <input
                type="text"
                value={headline}
                onChange={(e) => setHeadline(e.target.value)}
                placeholder="e.g. Helping engineers crack Senior & Staff level interviews"
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
              />
            </div>

            {/* Bio */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Bio / Mentoring Philosophy
              </label>
              <textarea
                rows={4}
                value={bio}
                onChange={(e) => setBio(e.target.value)}
                placeholder="Share your mentoring approach, past interview panels you have conducted, and expectations..."
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF] resize-none"
              />
            </div>

            {/* Expertise Tags */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Areas of Mentorship Expertise
              </label>
              <div className="flex gap-2 mb-3">
                <input
                  type="text"
                  value={expertiseInput}
                  onChange={(e) => setExpertiseInput(e.target.value)}
                  onKeyDown={handleAddExpertise}
                  placeholder="Type an area (e.g. Distributed Systems) and press Enter"
                  className="flex-1 px-4 py-2.5 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
                />
                <button
                  type="button"
                  onClick={handleAddExpertise}
                  className="px-5 py-2.5 rounded-xl bg-[#334155] text-white hover:bg-[#2DD4BF] hover:text-[#0F172A] font-medium transition-colors"
                >
                  Add
                </button>
              </div>

              <div className="flex flex-wrap gap-2 min-h-[38px] p-2 rounded-xl bg-[#0F172A]/50 border border-[#334155]">
                {expertise.map((skill) => (
                  <span
                    key={skill}
                    className="inline-flex items-center gap-1.5 px-3 py-1 rounded-lg bg-[#2DD4BF]/10 text-[#2DD4BF] border border-[#2DD4BF]/20 text-xs font-semibold"
                  >
                    {skill}
                    <button
                      type="button"
                      onClick={() => handleRemoveExpertise(skill)}
                      className="hover:text-red-400"
                    >
                      &times;
                    </button>
                  </span>
                ))}
              </div>
            </div>

            {/* Profile Image URL */}
            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-[#94A3B8] mb-2">
                Avatar Image URL (Optional)
              </label>
              <input
                type="url"
                value={profileImageUrl}
                onChange={(e) => setProfileImageUrl(e.target.value)}
                placeholder="https://images.unsplash.com/... or GitHub avatar"
                className="w-full px-4 py-3 rounded-xl bg-[#0F172A] border border-[#334155] text-white placeholder:text-[#94A3B8]/40 outline-none focus:border-[#2DD4BF]"
              />
            </div>
          </div>

          <div className="flex items-center justify-end gap-4">
            <button
              type="submit"
              disabled={saving}
              className="px-8 py-4 rounded-xl bg-[#2DD4BF] text-[#0F172A] font-bold text-sm shadow-[0_0_20px_rgba(45,212,191,0.3)] hover:scale-105 active:scale-95 transition-all disabled:opacity-60 flex items-center gap-2"
            >
              {saving ? (
                <>
                  <i className="fa-solid fa-spinner fa-spin" />
                  Saving Profile...
                </>
              ) : (
                <>
                  <i className="fa-solid fa-floppy-disk" />
                  <span>Save Mentor Profile</span>
                </>
              )}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
}
