import { useNavigate } from "react-router-dom";

export default function HomePage() {
    const navigate = useNavigate();

    const cards = [
        { title: "Pokédex", route: "/pokedex", desc: "Alle 151 Pokémon-Arten ansehen" },
        { title: "Pokémon in Besitz", route: "/owned", desc: "Deine gefangenen Pokémon" },
        { title: "Boxen", route: "/boxes", desc: "Deine Pokémon-Boxen verwalten" }
    ];

    return (
        <div className="homepage-root">
            <h1 className="homepage-title">Pokémon Management</h1>
            <div className="homepage-cards">
                {cards.map(card => (
                    <div
                        key={card.title}
                        className="homepage-card"
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
