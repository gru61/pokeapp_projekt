import { useEffect, useState } from "react";
import PokemonOverlay from "../components/PokemonOverlay";

/**
 * @component OwnedPage
 * @description
 * Übersicht und Verwaltung aller eigenen (gefangenen) Pokémon.
 *
 * Die Seite lädt beim Mounten alle relevanten Daten (Pokémon, Editionen, Boxen, Arten, Evolutionsregeln)
 * und zeigt eine große Grid-Ansicht aller eigenen Pokémon mit Edition, Box, Sprite, Name/Nickname, Level und Typ(en).
 *
 * Besonderheiten:
 * - Per Klick auf ein Pokémon öffnet sich ein Overlay ({@link PokemonOverlay}) zum Bearbeiten, Entwickeln oder Löschen.
 * - Nach Änderungen wird die Liste automatisch neu geladen.
 * - Fehler- und Ladezustände werden behandelt.
 * - Edition und Box werden jeweils als Text und für die Farblogik (per CSS-Klasse) verwendet.
 *
 * Typische Verwendung:
 * - Anzeige der aktuellen Sammlung ("Mein Inventar").
 * - Startpunkt für Editieren, Entwickeln oder Löschen von Pokémon.
 *
 * @example
 * <OwnedPage />
 */

export default function OwnedPage() {
    const [pokemons, setPokemons] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeMon, setActiveMon] = useState(null);

    const [editions, setEditions] = useState([]);
    const [boxes, setBoxes] = useState([]);
    const [evolutionRules, setEvolutionRules] = useState({});
    const [species, setSpecies] = useState([]);

    useEffect(() => {
        Promise.all([
            fetch("http://localhost:8080/api/pokemon").then(res => res.json()),
            fetch("http://localhost:8080/api/editions").then(r => r.json()),
            fetch("http://localhost:8080/api/boxnames").then(r => r.json()),
            fetch("http://localhost:8080/api/evolution-rules").then(r => r.json()),
            fetch("http://localhost:8080/api/species").then(r => r.json())
        ]).then(([pokes, edis, boxs, evos, specs]) => {
            setPokemons(pokes);
            setEditions(edis);
            setBoxes(boxs);
            setEvolutionRules(evos);
            setSpecies(specs);
            setLoading(false);
        }).catch(err => {
            setError(err.message);
            setLoading(false);
        });
    }, []);

    const reloadList = () => {
        fetch("http://localhost:8080/api/pokemon")
            .then(res => res.json())
            .then(setPokemons);
    };

    // Mapping von Edition auf CSS-Klasse (groß/klein sicherstellen)
    const editionToClass = ed =>
        ed ? "edition-" + ed.toLowerCase().replace("ü", "u").replace("ä", "a").replace("ö", "o") : "";

    if (loading) return <div className="loading">Lade deine Pokémon…</div>;
    if (error) return <div className="error">Fehler: {error}</div>;

    return (
        <div>
            <h1 className="owned-title">Deine Pokémon ({pokemons.length})</h1>
            <div className="pokemon-grid">
                {pokemons.map(mon => {
                    const spriteNr = String(mon.pokedexId).padStart(3, "0");
                    const showNickname = mon.nickname && mon.nickname.trim().length > 0;
                    const cardClass = `pokemon-card ${editionToClass(mon.edition)}`;
                    return (
                        <div
                            key={mon.id}
                            className={cardClass}
                            tabIndex={0}
                            onClick={() => setActiveMon(mon)}
                            style={{ position: "relative" }}
                        >
                            {/* Oben rechts: Pokédex-ID */}
                            <div className="pokemon-speciesid" style={{ right: 16, top: 14 }}>
                                #{spriteNr}
                            </div>
                            {/* Oben links: Edition & Box */}
                            <div style={{ position: "absolute", top: 16, left: 14, textAlign: "left", lineHeight: 1.22 }}>
                                <div className="pokemon-edition">{mon.edition}</div>
                                <div className="pokemon-box">{mon.box || mon.boxName}</div>
                            </div>
                            {/* Sprite */}
                            <img
                                src={`/sprites/${spriteNr}.png`}
                                alt={mon.speciesName}
                                className="pokemon-sprite"
                                onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                                style={{ marginTop: 32 }}
                            />
                            {/* Name/Nickname */}
                            <div className="pokemon-name">
                                {showNickname ? (
                                    <>
                                        <span className="pokemon-nickname">{mon.nickname}</span>
                                        <span className="pokemon-realname">{mon.speciesName}</span>
                                    </>
                                ) : (
                                    <span>{mon.speciesName}</span>
                                )}
                            </div>
                            {/* Level direkt unter Name */}
                            <div className="pokemon-level-inline" style={{ margin: "8px 0" }}>
                                Lvl. {mon.level}
                            </div>
                            {/* Typen */}
                            <div className="pokemon-types">
                                <span className={`poke-type ${mon.type1.toLowerCase()}`}>{mon.type1}</span>
                                {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 && (
                                    <span className={`poke-type ${mon.type2.toLowerCase()}`}>{mon.type2}</span>
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* --- Overlay für Details/Update/Entwicklung --- */}
            {activeMon && (
                <PokemonOverlay
                    mon={activeMon}
                    editions={editions}
                    boxes={boxes}
                    evolutionRules={evolutionRules}
                    species={species}
                    mode="update"
                    onUpdate={async (updatedData) => {
                        await fetch(`http://localhost:8080/api/pokemon/${updatedData.id}`, {
                            method: "PATCH",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify(updatedData)
                        });
                        reloadList(); // Das Overlay bleibt offen!
                    }}
                    onDelete={async () => {
                        if (window.confirm("Löschen?")) {
                            await fetch(`http://localhost:8080/api/pokemon/${activeMon.id}`, {
                                method: "DELETE"
                            });
                            reloadList();
                            setActiveMon(null); // Nach Löschen Overlay schließen!
                        }
                    }}
                    onEvolve={async (targetPokedexId, body) => {
                        await fetch(`http://localhost:8080/api/pokemon/${activeMon.id}`, {
                            method: "PATCH",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({
                                ...body,
                                pokedexId: targetPokedexId
                            })
                        });
                        reloadList();
                    }}
                    onClose={() => setActiveMon(null)}
                />
            )}
        </div>
    );
}
