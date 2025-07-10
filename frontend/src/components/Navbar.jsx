import { Link } from "react-router-dom";
import "./Navbar.css";

function Navbar() {
    return (
        <nav className="navbar">
            <h1 className="logo">Pokédex</h1>
            <ul className="nav-links">
                <li><Link to="/">Pokédex</Link></li>
                <li><Link to="/boxen">Boxen</Link></li>
                <li><Link to="/gefangen">Gefangen</Link></li>
            </ul>
        </nav>
    );
}

export default Navbar;