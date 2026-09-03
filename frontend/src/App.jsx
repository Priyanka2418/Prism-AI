import { BrowserRouter, Routes, Route } from "react-router-dom";

import Navbar from "./components/landing/Navbar";
import Hero from "./components/landing/Hero";
import Marquee from "./components/landing/Marquee";
import Features from "./components/landing/Features";
import Process from "./components/landing/Process";
import FAQ from "./components/landing/FAQ";
import Footer from "./components/landing/Footer";

import Signup from "./pages/Signup";
import Login from "./pages/Login";

function LandingPage() {
  return (
    <div className="scroll-smooth">
      <Navbar />
      <Hero />
      <Marquee />
      <Features />
      <Process />
      <FAQ />
      <Footer />
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/signup" element={<Signup />} />
         <Route path="/login" element={<Login />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
