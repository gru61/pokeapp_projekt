/**
 * @component HomePage
 * @description Startseite mit drei großen Karten: Pokédex, Eigene Pokémon und Boxen.
 * Jede Karte leitet auf die entsprechende Unterseite weiter.
 */

import { useNavigate } from "react-router-dom";
import pokedexImg from "../assets/pokedex.png";
import teamImg from "../assets/team.png";
import boxImg from "../assets/boxen.png";

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
