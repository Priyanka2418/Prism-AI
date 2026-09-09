import { Link } from "react-router-dom";

function Navbar() {
  return (
    <nav className="fixed left-0 top-0 z-50 flex w-full items-center justify-between border-b border-[#2C244C] bg-[#0D0B18]/70 px-6 py-4 backdrop-blur-md">
      {/* Logo */}
      <div className="flex items-center space-x-2">
        <span className="text-xl font-bold tracking-tight">
          Prism<span className="text-[#00F5FF]">.</span>AI
        </span>
      </div>

      {/* Navigation */}
      <div className="hidden items-center space-x-8 uppercase text-xs font-bold tracking-widest md:flex">
        <a
          href="#"
          className="relative rounded-full bg-white px-4 py-2 text-black"
        >
          Home
        </a>

        <a
          href="#pathways"
          className="relative text-white/80 transition-colors hover:text-white"
        >
          Features
        </a>

        <a
          href="#faq"
          className="relative text-white/80 transition-colors hover:text-white"
        >
          Faq
        </a>

        <a
          href="#about"
          className="relative text-white/80 transition-colors hover:text-white"
        >
          About
        </a>
      </div>

      {/* Auth Actions */}
      <div className="flex items-center gap-3">
        <Link
          to="/login"
          className="px-4 py-2 text-xs font-bold uppercase tracking-widest text-white/80 hover:text-white transition-colors"
        >
          Sign In
        </Link>
        <Link
          to="/signup"
          className="rounded-full bg-white px-5 py-2 text-xs font-bold uppercase tracking-widest text-black transition-all hover:bg-[#00F5FF]"
        >
          Get Started
        </Link>
      </div>
    </nav>
  );
}

export default Navbar;
