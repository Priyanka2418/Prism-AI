
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
          href="#"
          className="relative text-white/80 transition-colors hover:text-white"
        >
          Faq
        </a>

        <a
          href="#"
          className="relative text-white/80 transition-colors hover:text-white"
        >
          About
        </a>

      </div>

      {/* Sign Up */}
      <button
        className="rounded-full bg-white px-6 py-2 text-xs font-bold uppercase tracking-widest text-black transition-all hover:bg-[#00F5FF]"
      >
        Sign Up
      </button>

    </nav>
  );
}

export default Navbar;

