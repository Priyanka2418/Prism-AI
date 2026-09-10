import candidateImage from "../../assets/candidate.png";
import mentorImage from "../../assets/mentor.png";

function Features() {
  return (
    <section id="features" className="w-full px-6 py-24">
      <div className="mx-auto w-full max-w-7xl">
      {/* Section Heading */}
      <div className="mb-16">
        <h2 className="mb-4 text-sm font-bold uppercase tracking-[0.25em] text-[#8F8CA5]">
          Your Pathway
        </h2>

        <h3 className="text-4xl font-medium text-white md:text-5xl">
          Two ways to get started.
        </h3>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        {/* Candidate Path */}
        <div id="ai-interview" className="group relative h-[420px] overflow-hidden rounded-2xl md:h-[500px]">
          {/* Image */}
          <div className="absolute inset-0 h-full">
            <img
              src={candidateImage}
              alt="Candidate using Prism-AI"
              className="h-full w-full object-cover object-center transition-transform duration-700 group-hover:scale-105"
            />
          </div>
          {/* Gradient */}
          <div className="absolute inset-0 flex flex-col justify-end bg-linear-to-t from-black/90 via-black/40 to-transparent p-10">
            <span className="mb-2 text-xs font-bold uppercase tracking-widest text-[#00F5FF]">
              For Candidates
            </span>

            <h4 className="mb-4 text-3xl font-bold text-white">
              Ace your next interview.
            </h4>

            <p className="mb-6 max-w-md text-gray-300">
              Practice with AI-powered mock interviews tailored to your role,
              industry, and experience level.
            </p>
          </div>
        </div>

        {/* Mentor Path */}
        <div id="mentorship" className="group relative h-[420px] overflow-hidden rounded-2xl md:h-[500px]">
          {/* Image */}
          <div className="absolute inset-0 h-full">
            <img
              src={mentorImage}
              alt="Mentor helping candidates through Prism-AI"
              className="h-full w-full object-cover transition-transform duration-700 group-hover:scale-105"
            />
          </div>
          {/* Gradient */}
          <div className="absolute inset-0 flex flex-col justify-end bg-linear-to-t from-black/90 via-black/40 to-transparent p-8">
            <span className="mb-2 text-xs font-bold uppercase tracking-widest text-[#00F5FF]">
              For Mentors
            </span>

            <h4 className="mb-2 text-2xl font-bold text-white">
              Share your expertise.
            </h4>

            <p className="mb-6 text-sm text-gray-300">
              Guide candidates through real-world scenarios and build your
              reputation.
            </p>
          </div>
        </div>
      </div>
      </div>
    </section>
  );
}

export default Features;
