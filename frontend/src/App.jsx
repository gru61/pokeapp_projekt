/**
 * @file App.jsx
 * @description Hauptkomponente der Anwendung. Regelt das Routing und zeigt auf jeder Seite die MainNavbar an.
 */

import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainNavbar from "./components/MainNavbar";
import HomePage from "./pages/HomePage";
import PokedexPage from "./pages/PokedexPage";
import OwnedPage from "./pages/OwnedPage";
import BoxenPage from "./pages/BoxenPage";

export default function App() {
    return (
        <BrowserRouter>
            <MainNavbar />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/pokedex" element={<PokedexPage />} />
                <Route path="/owned" element={<OwnedPage />} />
                <Route path="/boxes" element={<BoxenPage />} />
            </Routes>
        </BrowserRouter>
    );
}
