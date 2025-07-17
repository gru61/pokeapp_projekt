import { useEffect, useState } from "react";
import PokemonOverlay from "../components/PokemonOverlay";

/**
 * @component OwnedPage
 * @description Übersicht und Verwaltung aller gefangenen Pokémon.
 * PATCH nutzt exakt die Backend-Konventionen (Enum-Strings).
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

    if (loading) return <div className="loading">Lade deine Pokémon…</div>;
    if (error) return <div className="error">Fehler: {error}</div>;

    return (
        <div className="owned-page">
            <h1 className="page-title">Deine Pokémon ({pokemons.length})</h1>
            <div className="pokemon-grid">
                {pokemons.map(mon => {
                    const spriteNr = String(mon.pokedexId).padStart(3, "0");
                    const showNickname = mon.nickname && mon.nickname.trim().length > 0;
                    return (
                        <div
                            key={mon.id}
                            className="pokemon-card"
                            tabIndex={0}
                            onClick={() => setActiveMon(mon)}
                        >
                            <div className="pokemon-speciesid">#{spriteNr}</div>
                            <img
                                src={`/sprites/${spriteNr}.png`}
                                alt={mon.speciesName}
                                className="pokemon-sprite"
                                onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                            />
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
                            <div className="pokemon-infos">
                                <span className="pokemon-level">Lvl. {mon.level}</span>
                                <span className="pokemon-edition">{mon.edition}</span>
                                <span className="pokemon-box">{mon.box || mon.boxName}</span>
                            </div>
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
                        reloadList();
                        setActiveMon(null);
                    }}
                    onDelete={async () => {
                        if (window.confirm("Löschen?")) {
                            await fetch(`http://localhost:8080/api/pokemon/${activeMon.id}`, {
                                method: "DELETE",
                            });
                            reloadList();
                            setActiveMon(null);
                        }
                    }}
                    onEvolve={async (targetPokedexId) => {
                        // Entwicklung PATCH, alle Felder
                        await fetch(`http://localhost:8080/api/pokemon/${activeMon.id}`, {
                            method: "PATCH",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({
                                pokedexId: targetPokedexId,
                                nickname: activeMon.nickname,
                                level: activeMon.level,
                                edition: activeMon.edition,
                                box: activeMon.box || activeMon.boxName
                            }),
                        });
                        reloadList();
                        setActiveMon(null);
                    }}
                    onClose={() => setActiveMon(null)}
                    reloadList={reloadList}
                />
            )}
        </div>
    );
}
