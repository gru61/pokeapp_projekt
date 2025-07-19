import { useNavigate } from "react-router-dom";
import pokedexImg from "../assets/pokedex.png";
import teamImg from "../assets/team.png";
import boxImg from "../assets/boxen.png";


/**
 * @component HomePage
 * @description
 * Startseite der App mit drei prominent platzierten Auswahlkarten:
 * - Pokédex: Übersicht aller Pokémon-Arten der ersten Generation
 * - Eigene Pokémon: Verwaltung und Anzeige aller gefangenen Pokémon
 * - Boxen: Boxen- und Team-Management mit Drag & Drop
 *
 * Jede Karte führt per Klick (oder Tastendruck "Enter") zur zugehörigen Unterseite.
 *
 * Besonderheiten:
 * - Die Karten zeigen ein passendes Bild und eine Kurzbeschreibung an.
 * - Navigation erfolgt über das React Router `useNavigate`-Hook.
 * - Barrierefrei: Karten sind auch per Tab-Fokus und Enter bedienbar.
 *
 * Typische Verwendung:
 * - Dies ist die Landing Page deiner Anwendung, Einstieg für alle Hauptfunktionen.
 *
 * @example
 * <HomePage />
 */

export default function HomePage() {
    const navigate = useNavigate();

    // Definition der Startkarten
    const cards = [
        { title: "Pokédex", route: "/pokedex", desc: "Die OG's", img: pokedexImg },
        { title: "Eigene Pokémon", route: "/owned", desc: "Deine gefangenen Pokémon", img: teamImg },
        { title: "Boxen", route: "/boxes", desc: "Deine Pokémon-Boxen verwalten", img: boxImg }
    ];

    return (
        <div className="homepage-root">
            <h1 className="homepage-title">Pokémon Management</h1>
            <div className="homepage-cards">
                {cards.map(card => (
                    <div
                        key={card.title}
                        className="homepage-card"
                        style={{ backgroundImage: `url(${card.img})` }}
                        onClick={() => navigate(card.route)}
                        tabIndex={0}
                        onKeyDown={e => e.key === "Enter" && navigate(card.route)}
                    >
                        <h2>{card.title}</h2>
                        <span>{card.desc}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}
