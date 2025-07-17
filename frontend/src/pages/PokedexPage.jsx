import { useEffect, useState } from "react";
import PokemonCard from "../components/PokemonCard";
import PokemonOverlay from "../components/PokemonOverlay";

/**
 * @component PokedexPage
 * @description Pokédex-Ansicht mit Grid aller Pokémon-Species. Overlay zum Hinzufügen in den Besitz bei Klick.
 */
export default function PokedexPage() {
    const [species, setSpecies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [editions, setEditions] = useState([]);
    const [boxes, setBoxes] = useState([]);
    const [activeMon, setActiveMon] = useState(null);

    // Lade alle Daten (Species, Editionen, Boxen)
    useEffect(() => {
        fetch("http://localhost:8080/api/species")
            .then(res => res.json())
            .then(setSpecies)
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));

        fetch("http://localhost:8080/api/editions").then(r => r.json()).then(setEditions);
        fetch("http://localhost:8080/api/boxnames").then(r => r.json()).then(setBoxes);
    }, []);


    if (loading) return <div>Lade Pokédex…</div>;
    if (error) return <div>Fehler: {error}</div>;

    return (
        <div>
            <h1 className="pokedex-title">Pokédex ({species.length} Arten)</h1>
            <div className="pokemon-grid">
                {species.map(mon => (
                    <PokemonCard key={mon.pokedexId} mon={mon} onClick={() => setActiveMon(mon)} />
                ))}
            </div>

            {/* Overlay zum Hinzufügen */}
            {activeMon && (
                <PokemonOverlay
                    mon={activeMon}
                    editions={editions}
                    boxes={boxes}
                    mode="add"
                    onSave={async (formData) => {
                        await fetch("http://localhost:8080/api/pokemon", {
                            method: "POST",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(formData)
                        });
                        setActiveMon(null);
                    }}
                    onClose={() => setActiveMon(null)}
                />
            )}
        </div>
    );
}
