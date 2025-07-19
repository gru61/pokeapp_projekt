import { BrowserRouter, Routes, Route } from "react-router-dom";
import MainNavbar from "./components/MainNavbar";
import HomePage from "./pages/HomePage";
import PokedexPage from "./pages/PokedexPage";
import OwnedPage from "./pages/OwnedPage";
import BoxenPage from "./pages/BoxenPage";


/**
 * @file App.jsx
 * @component App
 * @description
 * Hauptkomponente der Anwendung.
 *
 * Regelt das Routing mit react-router-dom und sorgt dafür, dass die MainNavbar auf allen Seiten sichtbar bleibt.
 * Je nach Route wird die jeweils passende Seite angezeigt:
 * - HomePage: Startseite der Anwendung
 * - PokedexPage: Pokédex-Ansicht aller Pokémon-Arten
 * - OwnedPage: Übersicht und Verwaltung aller eigenen (gefangenen) Pokémon
 * - BoxenPage: Verwaltung und Organisation aller Boxen und Teams
 *
 * Besonderheiten:
 * - Nutzt `<BrowserRouter>`, `<Routes>` und `<Route>` für das Routing.
 * - Die Navigationsleiste (`MainNavbar`) bleibt immer am oberen Rand sichtbar.
 * - Jede Seite ist eine eigenständige Komponente und übernimmt die jeweilige Fachlogik.
 *
 * @example
 * <App />
 */

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
