import Navbar from "./components/landing/Navbar";
import Hero from "./components/landing/Hero";
import Marquee from "./components/landing/Marquee";
import Features from "./components/landing/Features";
import Process from "./components/landing/Process";
import FAQ from "./components/landing/FAQ";
import Footer from "./components/landing/Footer";

function App() {
  return (
    <>
      <Navbar />
      <Hero />
      <Marquee />
      <Features />
      <Process />
      <FAQ/>
      <Footer/>
    </>
  );
}

export default App;
