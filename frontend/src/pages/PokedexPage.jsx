import { useEffect, useState } from "react";
import "../styles/PokedexPage.css";

export default function PokedexPage() {
    const [species, setSpecies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Overlay & Formular
    const [activeMon, setActiveMon] = useState(null);
    const [showAddForm, setShowAddForm] = useState(false);

    // Dropdown-Optionen
    const [editions, setEditions] = useState([]);
    const [boxes, setBoxes] = useState([]);

    // Lade Pokédex-Daten
    useEffect(() => {
        fetch("http://localhost:8080/api/species")
            .then(res => {
                if (!res.ok) throw new Error("Fehler beim Laden!");
                return res.json();
            })
            .then(setSpecies)
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));
    }, []);

    // Lade Editionen und Boxen für Formular
    useEffect(() => {
        fetch("http://localhost:8080/api/editions")
            .then(res => res.json())
            .then(setEditions);
        fetch("http://localhost:8080/api/boxnames")
            .then(res => res.json())
            .then(setBoxes);
    }, []);

    if (loading) return <div style={{ textAlign: "center", marginTop: 50 }}>Lade Pokédex…</div>;
    if (error) return <div style={{ textAlign: "center", marginTop: 50, color: "#ffb400" }}>Fehler: {error}</div>;

    return (
        <div style={{ padding: "40px 0" }}>
            <h1 className="pokedex-title">Pokédex (151 Arten)</h1>
            <div className="pokedex-grid">
                {species.map(mon => {
                    const spriteNr = String(mon.pokedexId).padStart(3, "0");
                    return (
                        <div key={mon.pokedexId}
                             className="pokedex-card"
                             tabIndex={0}
                             onClick={() => { setActiveMon(mon); setShowAddForm(false); }}
                        >
                            <div className="pokedex-speciesid">#{spriteNr}</div>
                            <img
                                src={`/sprites/${spriteNr}.png`}
                                alt={mon.name}
                                className="pokedex-sprite"
                                onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                            />
                            <div className="pokedex-name">{mon.name}</div>
                            <div className="pokedex-types">
                                <span className={"poke-type " + mon.type1.toLowerCase()}>{mon.type1}</span>
                                {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 ?
                                    <span className={"poke-type " + mon.type2.toLowerCase()}>{mon.type2}</span> : null}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Overlay */}
            {activeMon && (
                <div className="overlay-bg" onClick={() => { setActiveMon(null); setShowAddForm(false); }}>
                    <div className="overlay-card" onClick={e => e.stopPropagation()}>
                        <button className="overlay-close" onClick={() => { setActiveMon(null); setShowAddForm(false); }}>×</button>
                        {/* Große Karte */}
                        <div className="pokedex-card overlay-pokemon-card">
                            <div className="pokedex-speciesid">
                                #{String(activeMon.pokedexId).padStart(3, "0")}
                            </div>
                            <img
                                src={`/sprites/${String(activeMon.pokedexId).padStart(3, "0")}.png`}
                                alt={activeMon.name}
                                className="pokedex-sprite"
                                style={{ width: 130, height: 130 }}
                                onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                            />
                            <div className="pokedex-name" style={{ fontSize: "2rem" }}>{activeMon.name}</div>
                            <div className="pokedex-types" style={{ marginTop: 16 }}>
                                <span className={"poke-type " + activeMon.type1.toLowerCase()}>{activeMon.type1}</span>
                                {activeMon.type2 && activeMon.type2 !== "NORMAL" && activeMon.type2 !== activeMon.type1 ?
                                    <span className={"poke-type " + activeMon.type2.toLowerCase()}>{activeMon.type2}</span> : null}
                            </div>
                        </div>

                        {/* Hinzufügen-Button/Formular */}
                        {showAddForm ? (
                            <AddPokemonForm
                                mon={activeMon}
                                editions={editions}
                                boxes={boxes}
                                onDone={() => {
                                    setShowAddForm(false);
                                    setActiveMon(null);
                                }}
                            />
                        ) : (
                            <button className="add-btn" onClick={() => setShowAddForm(true)}>
                                Pokémon hinzufügen
                            </button>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

// Komponente fürs Hinzufügen-Formular
function AddPokemonForm({ mon, editions, boxes, onDone }) {
    const [nickname, setNickname] = useState("");
    const [level, setLevel] = useState(1);
    const [edition, setEdition] = useState(editions[0] || "");
    const [box, setBox] = useState(boxes[0] || "");
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async e => {
        e.preventDefault();
        setSaving(true);
        setError(null);

        try {
            const resp = await fetch("http://localhost:8080/api/pokemon", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    nickname,
                    level,
                    edition,
                    box: box,
                    pokedexId: mon.pokedexId
                })
            });
            if (!resp.ok) throw new Error("Hinzufügen fehlgeschlagen");
            onDone();
        } catch (err) {
            setError(err.message);
        } finally {
            setSaving(false);
        }
    };

    return (
        <form className="add-pokemon-form" onSubmit={handleSubmit}>
            <div>
                <label>Nickname (optional):</label>
                <input value={nickname} onChange={e => setNickname(e.target.value)} />
            </div>
            <div>
                <label>Level:</label>
                <select value={level} onChange={e => setLevel(Number(e.target.value))}>
                    {Array.from({ length: 100 }, (_, i) => i + 1).map(i =>
                        <option key={i} value={i}>{i}</option>
                    )}
                </select>
            </div>
            <div>
                <label>Edition:</label>
                <select value={edition} onChange={e => setEdition(e.target.value)}>
                    {editions.map(ed =>
                        <option key={ed} value={ed}>{ed}</option>
                    )}
                </select>
            </div>
            <div>
                <label>Box:</label>
                <select value={box} onChange={e => setBox(e.target.value)}>
                    {boxes.map(bx =>
                        <option key={bx} value={bx}>{bx}</option>
                    )}
                </select>
            </div>
            {error && <div style={{ color: "red", margin: "10px 0" }}>{error}</div>}
            <button type="submit" disabled={saving}>Speichern</button>
            <button type="button" onClick={onDone} disabled={saving} style={{ marginLeft: 16 }}>
                Abbrechen
            </button>
        </form>
    );
}
