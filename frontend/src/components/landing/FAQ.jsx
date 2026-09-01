import { useState } from "react";

const questions = [
  {
    question: "How does Prism-AI conduct mock interviews?",
    answer:
      "Prism-AI uses AI to simulate realistic interview conversations based on your role, experience level, and interview type. The AI adapts its questions based on your responses to create a more realistic practice experience.",
  },
  {
    question: "Can I practice for a specific job role?",
    answer:
      "Yes. You can practice for different roles and interview types, with questions tailored to the skills and expectations associated with your target position.",
  },
  {
    question: "How does the AI evaluate my performance?",
    answer:
      "After your interview, Prism-AI analyzes your responses and provides structured feedback on areas such as communication, technical accuracy, confidence, and overall interview performance.",
  },
  {
    question: "Can I get feedback from a real mentor?",
    answer:
      "Yes. Candidates can connect with verified industry mentors for additional feedback and guidance beyond the AI-generated interview analysis.",
  },
  {
    question: "How are mentors verified?",
    answer:
      "Mentor applications are reviewed before they can offer mentoring sessions. The verification process helps ensure that candidates can connect with mentors who have relevant professional experience.",
  },
];

function FAQ() {
  const [openIndex, setOpenIndex] = useState(null);

  return (
    <section className="bg-[#030207] px-6 py-20">
      <div className="mx-auto max-w-3xl">

        {/* Heading */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-xs font-semibold tracking-[0.3em] text-gray-500">
            COMMON QUESTIONS
          </p>

          <h2 className="text-3xl font-medium text-white md:text-4xl">
            Clear the path for your success.
          </h2>
        </div>

        {/* FAQ */}
        <div className="space-y-3">
          {questions.map((item, index) => {
            const isOpen = openIndex === index;

            return (
              <div
                key={item.question}
                className="overflow-hidden rounded-xl bg-[#151126]"
              >
                {/* Question */}
                <button
                  onClick={() =>
                    setOpenIndex(isOpen ? null : index)
                  }
                  className="flex w-full items-center justify-between px-4 py-5 text-left"
                >
                  <span className="text-sm font-semibold text-white">
                    {item.question}
                  </span>

                  {/* Chevron */}
                  <svg
                    className={`h-4 w-4 text-gray-400 transition-transform duration-300 ${
                      isOpen ? "rotate-180" : ""
                    }`}
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth="2"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="m19 9-7 7-7-7"
                    />
                  </svg>
                </button>

                {/* Answer */}
                <div
                  className={`grid transition-all duration-300 ease-in-out ${
                    isOpen
                      ? "grid-rows-[1fr] opacity-100"
                      : "grid-rows-[0fr] opacity-0"
                  }`}
                >
                  <div className="overflow-hidden">
                    <p className="px-4 pb-5 text-xs leading-5 text-gray-400">
                      {item.answer}
                    </p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

      </div>
    </section>
  );
}

export default FAQ;