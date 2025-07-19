import { useEffect, useState } from "react";
import PokemonCard from "../components/PokemonCard";
import PokemonOverlay from "../components/PokemonOverlay";

/**
 * @component PokedexPage
 * @description
 * Übersicht aller verfügbaren Pokémon-Arten (Species) im Pokédex.
 *
 * Die Seite zeigt ein Grid mit allen Pokémon-Species.
 * Beim Klick auf eine Karte öffnet sich ein Overlay, mit dem das ausgewählte Pokémon
 * in den eigenen Besitz (Team/Box) übernommen werden kann (z.B. für Fang-/Sammel-Modus).
 *
 * Besonderheiten:
 * - Lädt beim Start alle Pokémon-Arten sowie die verfügbaren Editionen und Boxnamen.
 * - Nutzt {@link PokemonCard} für die Darstellung jedes einzelnen Pokémon.
 * - Nutzt {@link PokemonOverlay} für das Hinzufügen in den Besitz.
 * - Behandelt Lade- und Fehlerzustände.
 *
 * Typische Verwendung:
 * - Hauptansicht zum Durchblättern, Suchen und gezielten "Fangen"/Hinzufügen von Pokémon.
 *
 * @example
 * <PokedexPage />
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
