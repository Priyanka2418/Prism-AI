const processSteps = [
  {
    title: "Practice Session",
    description:
      "Engage in role-specific AI mock interviews that adapt to your answers in real-time.",
    icon: "fa-solid fa-microphone",
  },
  {
    title: "Instant Analysis",
    description:
      "Receive immediate telemetry on your performance, tone, and technical accuracy.",
    icon: "fa-solid fa-bolt",
  },
  {
    title: "Expert Review",
    description:
      "Opt-in for deep calibration from our network of verified industry mentors.",
    icon: "fa-solid fa-user-tie",
  },
  {
    title: "Track Progress",
    description:
      "Monitor your growth across multiple dimensions and benchmark against your goals.",
    icon: "fa-solid fa-chart-line",
  },
];

function Process() {
  return (
    <section id = "about" className="bg-[#0D0B18] px-6 py-20">
      <div className="mx-auto max-w-7xl">
        {/* Section heading */}
        <div className="mb-8">
          <p className="mb-3 text-[10px] font-semibold uppercase tracking-[0.3em] text-[#8F8CA5]">
            The Process
          </p>

          <h2 className="text-3xl font-medium tracking-tight text-white md:text-4xl">
            How Prism.AI accelerates your growth.
          </h2>
        </div>

        {/* Process cards */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
          {processSteps.map((step) => (
            <div
              key={step.title}
              className="
    group h-52 rounded-2xl
    border border-[#2C244C]
    bg-[#16122C]
    p-6
    transition-all duration-300

    hover:border-[#00F5FF]/50
    hover:bg-[#17152F]
  "
            >
              {/* Icon */}
              <div
                className="
          mb-6 flex h-11 w-11
          items-center justify-center
          rounded-xl
          border border-[#2C244C]
          bg-[#00F5FF]/10
          text-base text-[#00F5FF]
          transition-all duration-300

          group-hover:border-[#00F5FF]/50
          group-hover:bg-[#00F5FF]/15
          group-hover:scale-110
        "
              >
               <i className={step.icon}></i>
              </div>

              <h3 className="mb-3 text-base font-semibold text-white">
                {step.title}
              </h3>

              <p className="text-xs leading-[1.7] text-[#8F8CA5]">
                {step.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

export default Process;
