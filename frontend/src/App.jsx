import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Pokedex from "./pages/Pokedex";
import OwnedPokemonDetails from "./pages/OwnedPokemonDetails.jsx";
import SpeciesDetails from "./pages/SpeciesDetails";
import Boxen from "./pages/Boxen";
import Gefangen from "./pages/Gefangen";
import "./App.css";

function App() {
    return (
        <BrowserRouter>
            <Navbar />
            <div className="container">
                <Routes>
                    <Route path="/" element={<Pokedex />} />
                    <Route path="/details/:id" element={<OwnedPokemonDetails />} />
                    <Route path="/species/:id" element={<SpeciesDetails />} />
                    <Route path="/boxen" element={<Boxen />} />
                    <Route path="/gefangen" element={<Gefangen />} />
                </Routes>
            </div>
        </BrowserRouter>
    );
}

export default App;