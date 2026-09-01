import heroImage from "../../assets/hero.png";
import { useState, useEffect } from "react";

function Hero() {
  const [wordIndex, setWordIndex] = useState(0);
  const [displayText, setDisplayText] = useState("");
  const [isDeleting, setIsDeleting] = useState(false);
  const words = ["Candidate", "Mentor"];

  useEffect(() => {
    const currentWord = words[wordIndex];

    const speed = isDeleting ? 150 : 250;

    const timeout = setTimeout(() => {
      if (!isDeleting) {
        setDisplayText(currentWord.substring(0, displayText.length + 1));

        if (displayText.length === currentWord.length) {
          setIsDeleting(true);
        }
      } else {
        setDisplayText(currentWord.substring(0, displayText.length - 1));

        if (displayText.length === 0) {
          setIsDeleting(false);
          setWordIndex((currentIndex) => (currentIndex + 1) % words.length);
        }
      }
    }, speed);

    return () => clearTimeout(timeout);
  }, [displayText, isDeleting, wordIndex]);

  return (
    <section className="relative overflow-hidden bg-[#05040A] px-6 pb-16 pt-24">
      <div className="mx-auto flex max-w-7xl flex-col items-center justify-center md:flex-row">
        {/* TEXT*/}
        <div className="w-full md:w-1/2 md:pr-12">
          <p className="mb-4 text-sm font-bold uppercase tracking-[0.25em] text-[#8F8CA5]">
            Begin Your Journey
          </p>
          <h1 className="max-w-2xl text-5xl font-medium leading-tight text-white md:text-7xl">
            Choose your path. Become a better{" "}
            <span className="inline-block w-[9ch]">
              <span className="font-bold text-[#00F5FF]">{displayText}</span>
              <span className="animate-pulse">|</span>
            </span>
          </h1>
          <p className="mb-10 mt-6 max-w-lg text-lg leading-relaxed text-[#8F8CA5]">
            Whether you're preparing for your dream role or guiding others to
            success, Prism-AI builds the experience around you.
          </p>
          <div className="flex flex-wrap gap-4">
            <button
              className="rounded-full border border-white/20 px-8 py-4 text-xs font-bold uppercase tracking-widest
             text-white transition-all duration-300 hover:-translate-y-1 hover:bg-white hover:text-black"
            >
              Candidate
            </button>
            <button
              className="rounded-full border border-white/20 px-8 py-4 text-xs font-bold uppercase tracking-widest
             text-white transition-all duration-300 hover:-translate-y-1 hover:bg-white hover:text-black"
            >
              Mentor
            </button>
          </div>
        </div>
        {/* IMAGE*/}

        <div className="relative mt-12 aspect-video w-full max-w-2xl cursor-pointer group md:ml-auto md:mt-0">
          {/* Decorative layer */}
          <div
            className="absolute inset-0 bg-[#00F5FF]/5 rounded-xl -translate-x-10 -translate-y-10
          transition-transform duration-500 group-hover:translate-x-0 group-hover:translate-y-0"
          ></div>

          {/* Image */}
          <div
            className="relative h-full overflow-hidden rounded-xl shadow-2xl border border-[#2C244C] 
          transform transition-transform duration-500 group-hover:scale-[1.02]"
          >
            <img
              src={heroImage}
              alt="Prism-AI interview platform"
              className="w-full h-full object-cover"
            />
          </div>
        </div>
      </div>
    </section>
  );
}

export default Hero;
