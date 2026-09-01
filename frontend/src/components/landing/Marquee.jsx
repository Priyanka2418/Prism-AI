const items = [
  "PRISM-AI",
  "CANDIDATE PORTAL",
  "AI INTERVIEWS",
  "MENTOR NETWORK",
];

function Marquee() {
  return (
    <section className="overflow-hidden border-y border-[#2C244C] py-12">
      <div className="marquee-track flex w-max whitespace-nowrap">
        {/* First copy */}
        <div className="flex items-center gap-12 px-6">
          {items.map((item) => (
            <div key={item} className="flex items-center gap-12">
              <span className="text-6xl font-bold text-outline md:text-8xl">
                {item}
              </span>

             <span className="h-6 w-6 shrink-0 rounded-full bg-[#00F5FF] animate-pulse" />
            </div>
          ))}
        </div>

        {/* Second copy */}
        <div className="flex items-center gap-12 px-6" aria-hidden="true">
          {items.map((item) => (
            <div key={item} className="flex items-center gap-12">
              <span className="text-6xl font-bold text-outline md:text-8xl">
                {item}
              </span>
              <span className="h-6 w-6 shrink-0 rounded-full bg-[#00F5FF] animate-pulse" />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default Marquee;
