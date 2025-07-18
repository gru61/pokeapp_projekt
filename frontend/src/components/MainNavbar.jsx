import { NavLink } from "react-router-dom";

/**
 * @component MainNavbar
 * @description
 * Globale Navigationsleiste für die gesamte Anwendung.
 * Die Navbar bleibt permanent am oberen Rand sichtbar und bietet
 * auf allen Seiten den schnellen Zugriff auf die wichtigsten Bereiche:
 * - Home
 * - Pokédex
 * - Eigene Pokémon
 * - Boxen
 *
 * Verwendet `NavLink` aus `react-router-dom`, um die aktuelle Route
 * visuell hervorzuheben ("active"-Klasse).
 *
 * @example
 * <MainNavbar />
 *
 * @remarks
 * - Die CSS-Klassen (z.B. `mainnavbar-link` und `active`) steuern das Styling.
 * - Die Komponente wird typischerweise im App-Layout oder direkt im `App.jsx` verwendet,
 *   damit alle Seiten die gleiche Navigation nutzen.
 */
export default function MainNavbar() {
    return (
        <nav className="mainnavbar-root">
            <NavLink to="/" className={({ isActive }) => "mainnavbar-link" + (isActive ? " active" : "")}>
                Home
            </NavLink>
            <NavLink to="/pokedex" className={({ isActive }) => "mainnavbar-link" + (isActive ? " active" : "")}>
                Pokédex
            </NavLink>
            <NavLink to="/owned" className={({ isActive }) => "mainnavbar-link" + (isActive ? " active" : "")}>
                Eigene Pokémon
            </NavLink>
            <NavLink to="/boxes" className={({ isActive }) => "mainnavbar-link" + (isActive ? " active" : "")}>
                Boxen
            </NavLink>
        </nav>
    );
}
