const Footer = () => {
  const scrollTo = (id) => {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <footer className="bg-[#151126] border-t border-white/10 pt-20 pb-10 px-6">
      {/* Main Footer */}
      <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-12 mb-16">
        {/* Brand */}
        <div className="col-span-1 md:col-span-1">
          <span className="text-xl font-bold tracking-tight block mb-6">
            Prism-AI<span className="text-accent">.</span>
          </span>

          <p className="text-sm text-mute">
            Practice smarter. Interview better. Get feedback that helps you
            improve.
          </p>
        </div>
        {/* Product */}
        <div>
          <h5 className="font-bold uppercase text-xs tracking-widest mb-6">
            Product
          </h5>

          <ul className="space-y-4 text-sm text-mute">
            <li>
              <button
                onClick={() => scrollTo("features")}
                className="hover:text-white transition-colors text-left"
              >
                Features
              </button>
            </li>

            <li>
              <button
                onClick={() => scrollTo("how-it-works")}
                className="hover:text-white transition-colors text-left"
              >
                How it works
              </button>
            </li>

            <li>
              <button
                onClick={() => scrollTo("ai-interview")}
                className="hover:text-white transition-colors text-left"
              >
                AI Interview
              </button>
            </li>

            <li>
              <button
                onClick={() => scrollTo("mentorship")}
                className="hover:text-white transition-colors text-left"
              >
                Mentorship
              </button>
            </li>
          </ul>
        </div>
        {/* Company */}
        <div>
          <h5 className="font-bold uppercase text-xs tracking-widest mb-6">
            Company
          </h5>

          <ul className="space-y-4 text-sm text-mute">
            <li>
              <a href="#about" className="hover:text-white transition-colors">
                About Prism-AI
              </a>
            </li>

            <li>
              <a href="#faq" className="hover:text-white transition-colors">
                FAQ
              </a>
            </li>

            <li>
              <a href="#contact" className="hover:text-white transition-colors">
                Contact
              </a>
            </li>
          </ul>
        </div>
        {/* Connect */}
        <div>
          <h5 className="font-bold uppercase text-xs tracking-widest mb-6">
            Connect
          </h5>

          <div className="flex space-x-6 text-xl">
            {/* GitHub */}
            <a
              href="https://github.com/Priyanka2418/Prism-AI"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-cyan-400 transition-colors"
              aria-label="GitHub Repository"
            >
              <i className="fa-brands fa-github"></i>
            </a>

            {/* LinkedIn */}
            <a
              href="#"
              className="hover:text-cyan-400 transition-colors"
              aria-label="LinkedIn"
            >
              <i className="fa-brands fa-linkedin"></i>
            </a>
          </div>
        </div>
      </div>

      {/* Bottom Footer */}
      <div className="max-w-7xl mx-auto border-t border-white/10 pt-8 flex flex-row justify-between items-center text-xs text-mute">
        <p>&copy; 2026 Prism-AI. All rights reserved.</p>

        <div className="flex space-x-6">
          <a href="#" className="hover:text-white transition-colors">
            Privacy Policy
          </a>

          <a href="#" className="hover:text-white transition-colors">
            Terms of Service
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
