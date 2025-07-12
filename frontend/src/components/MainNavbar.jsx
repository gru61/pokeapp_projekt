import { NavLink } from "react-router-dom";

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
                Pokémon in Besitz
            </NavLink>
            <NavLink to="/boxes" className={({ isActive }) => "mainnavbar-link" + (isActive ? " active" : "")}>
                Boxen
            </NavLink>
        </nav>
    );
}
