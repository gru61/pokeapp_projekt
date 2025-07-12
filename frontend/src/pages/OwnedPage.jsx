import { useEffect, useState } from "react";
import "../styles/OwnedPage.css";

/**
 * @component OwnedPage
 * @description Hauptkomponente zur Anzeige und Verwaltung der Pokémon im Besitz des Benutzers.
 * Ermöglicht das Anzeigen, Bearbeiten und Löschen von Pokémon sowie deren Entwicklung.
 * 
 * @returns {JSX.Element} Eine responsive Grid-Ansicht aller Pokémon mit Detail-Overlay
 */
export default function OwnedPage() {
    /** @type {[Array, Function]} State für die Liste der Pokémon */
    const [pokemons, setPokemons] = useState([]);
    /** @type {[boolean, Function]} Loading-Status der Komponente */
    const [loading, setLoading] = useState(true);
    /** @type {[string|null, Function]} Fehlermeldungen */
    const [error, setError] = useState(null);

    // Overlay/Detail-Logik
    /** @type {[Object|null, Function]} Aktiv ausgewähltes Pokémon für Detail-Ansicht */
    const [activeMon, setActiveMon] = useState(null);

    // Enums und weitere Daten
    const [editions, setEditions] = useState([]);
    const [boxes, setBoxes] = useState([]);
    const [evolutionRules, setEvolutionRules] = useState({});
    const [species, setSpecies] = useState([]);

    // Daten laden
    useEffect(() => {
        fetch("http://localhost:8080/api/pokemon")
            .then(res => res.json())
            .then(setPokemons)
            .catch(err => setError(err.message))
            .finally(() => setLoading(false));

        fetch("http://localhost:8080/api/editions").then(r => r.json()).then(setEditions);
        fetch("http://localhost:8080/api/boxnames").then(r => r.json()).then(setBoxes);
        fetch("http://localhost:8080/api/evolution-rules").then(r => r.json()).then(setEvolutionRules);
        fetch("http://localhost:8080/api/species").then(r => r.json()).then(setSpecies);
    }, []);

    /**
     * Lädt die Liste der Pokémon neu
     * @function reloadList
     */
    const reloadList = () => {
        setLoading(true);
        fetch("http://localhost:8080/api/pokemon")
            .then(res => res.json())
            .then(setPokemons)
            .finally(() => setLoading(false));
    };

    if (loading) return <div style={{ textAlign: "center", marginTop: 50 }}>Lade deine Pokémon…</div>;
    if (error) return <div style={{ textAlign: "center", marginTop: 50, color: "#ffb400" }}>Fehler: {error}</div>;

    return (
        <div style={{ padding: "40px 0" }}>
            <h1 style={{ color: "#f8f8f8", textAlign: "center", marginBottom: 40 }}>
                Deine Pokémon ({pokemons.length})
            </h1>
            <div className="owned-grid">
                {pokemons.map(mon => {
                    const spriteNr = String(mon.pokedexId).padStart(3, "0");
                    const showNickname = mon.nickname && mon.nickname.trim().length > 0;
                    return (
                        <div key={mon.id} className="owned-card" tabIndex={0}
                             onClick={() => setActiveMon(mon)}>
                            <div className="owned-speciesid">#{spriteNr}</div>
                            <img
                                src={`/sprites/${spriteNr}.png`}
                                alt={mon.speciesName}
                                className="owned-sprite"
                                onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                            />
                            <div className="owned-name">
                                {showNickname ? (
                                    <>
                                        <span className="owned-nickname">{mon.nickname}</span>
                                        <span className="owned-realname">{mon.speciesName}</span>
                                    </>
                                ) : (
                                    <span>{mon.speciesName}</span>
                                )}
                            </div>
                            <div className="owned-infos">
                                <span className="owned-level">Lvl. {mon.level}</span>
                                <span className="owned-edition">{mon.edition}</span>
                                <span className="owned-box">{mon.boxName}</span>
                            </div>
                            <div className="owned-types">
                                <span className={"poke-type " + mon.type1.toLowerCase()}>{mon.type1}</span>
                                {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 ? (
                                    <span className={"poke-type " + mon.type2.toLowerCase()}>{mon.type2}</span>
                                ) : null}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* --- Overlay für Details, Update, Entwicklung --- */}
            {activeMon && (
                <PokemonOverlay
                    mon={activeMon}
                    editions={editions}
                    boxes={boxes}
                    evolutionRules={evolutionRules}
                    species={species}
                    onClose={() => setActiveMon(null)}
                    reloadList={reloadList}
                />
            )}
        </div>
    );
}

/**
 * @component PokemonOverlay
 * @description Detail-Ansicht eines einzelnen Pokémon mit Bearbeitungsmöglichkeiten
 * 
 * @param {Object} props - Komponenten-Properties
 * @param {Object} props.mon - Das anzuzeigende Pokémon-Objekt
 * @param {Array} props.editions - Verfügbare Editionen
 * @param {Array} props.boxes - Verfügbare Boxen
 * @param {Object} props.evolutionRules - Entwicklungsregeln für Pokémon
 * @param {Array} props.species - Liste aller Pokémon-Arten
 * @param {Function} props.onClose - Callback zum Schließen des Overlays
 * @param {Function} props.reloadList - Callback zum Neuladen der Pokémon-Liste
 * 
 * @returns {JSX.Element} Ein modales Overlay mit Detailansicht und Bearbeitungsoptionen
 */
function PokemonOverlay({ mon, editions, boxes, evolutionRules, species, onClose, reloadList }) {
    const [editField, setEditField] = useState(null);
    const [fieldValue, setFieldValue] = useState("");
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState(null);
    const [localMon, setLocalMon] = useState(mon);

    // Start Inline-Edit
    const startEdit = (field, value) => {
        setEditField(field);
        setFieldValue(value);
    };

    // Speichern eines Felds
    const saveField = async () => {
        setUpdating(true);
        setError(null);
        let updateData = {
            pokedexId: localMon.pokedexId,
            nickname: localMon.nickname,
            level: localMon.level,
            edition: localMon.edition,
            box: localMon.boxName
        };
        updateData[editField === "boxName" ? "box" : editField] = fieldValue;

        try {
            const resp = await fetch(`http://localhost:8080/api/pokemon/${localMon.id}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updateData)
            });
            if (!resp.ok) throw new Error("Update fehlgeschlagen");
            setLocalMon({ ...localMon, [editField]: fieldValue, ...(editField === "box" && { boxName: fieldValue }) });
            setEditField(null);
            reloadList();
        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    };

    /**
     * Entwickelt das aktuelle Pokémon zu einer neuen Form
     * @async
     * @function handleEvolve
     * @param {number} targetId - Pokédex-ID der Zielentwicklung
     */
    async function handleEvolve(targetId) {
        setUpdating(true);
        setError(null);
        const body = {
            pokedexId: targetId,
            nickname: localMon.nickname,
            level: localMon.level,
            edition: localMon.edition,
            box: localMon.boxName
        };
        try {
            const resp = await fetch(`http://localhost:8080/api/pokemon/${localMon.id}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body)
            });
            if (!resp.ok) throw new Error("Entwicklung fehlgeschlagen");
            onClose();
            reloadList();
        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    }

    // Löschen
    const handleDelete = async () => {
        if (!window.confirm("Dieses Pokémon wirklich löschen?")) return;
        setUpdating(true);
        try {
            await fetch(`http://localhost:8080/api/pokemon/${localMon.id}`, { method: "DELETE" });
            onClose();
            reloadList();
        } finally {
            setUpdating(false);
        }
    };

    // Entwicklungsmöglichkeiten
    const evolutions = evolutionRules[localMon.pokedexId] || [];

    return (
        <div className="overlay-bg" onClick={onClose}>
            <div className="overlay-card" onClick={e => e.stopPropagation()}>
                <button className="overlay-close" onClick={onClose}>×</button>
                <div className="owned-card overlay-pokemon-card" style={{ marginBottom: 20 }}>
                    <div className="owned-speciesid">
                        #{String(localMon.pokedexId).padStart(3, "0")}
                    </div>
                    <img
                        src={`/sprites/${String(localMon.pokedexId).padStart(3, "0")}.png`}
                        alt={localMon.speciesName}
                        className="owned-sprite"
                        style={{ width: 130, height: 130 }}
                        onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                    />
                    <div className="owned-name" style={{ fontSize: "2rem" }}>
                        {editField === "nickname"
                            ? (
                                <>
                                    <input
                                        autoFocus
                                        value={fieldValue}
                                        onChange={e => setFieldValue(e.target.value)}
                                        onBlur={saveField}
                                        onKeyDown={e => e.key === "Enter" && saveField()}
                                        style={{ fontSize: "1.2rem", marginBottom: 4 }}
                                    />
                                    <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                                </>
                            )
                            : (
                                <>
                                    <span className="owned-nickname" onClick={() => startEdit("nickname", localMon.nickname)}>
                                        {localMon.nickname || <span style={{ color: "#bbb" }}>Kein Nickname</span>}
                                    </span>
                                    <span className="edit-icon" title="Nickname ändern" onClick={() => startEdit("nickname", localMon.nickname)}>✏️</span>
                                    <span className="owned-realname">{localMon.speciesName}</span>
                                </>
                            )}
                    </div>
                    <div className="owned-infos">
                        <span className="owned-level">
                            {editField === "level"
                                ? (
                                    <>
                                        <select value={fieldValue} onChange={e => setFieldValue(Number(e.target.value))} onBlur={saveField}>
                                            {Array.from({ length: 100 }, (_, i) => i + 1).map(i =>
                                                <option key={i} value={i}>{i}</option>
                                            )}
                                        </select>
                                        <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                                    </>
                                )
                                : (
                                    <>
                                        Lvl. <span onClick={() => startEdit("level", localMon.level)}>{localMon.level}</span>
                                        <span className="edit-icon" title="Level ändern" onClick={() => startEdit("level", localMon.level)}>✏️</span>
                                    </>
                                )
                            }
                        </span>
                        <span className="owned-edition">
                            {editField === "edition"
                                ? (
                                    <>
                                        <select value={fieldValue} onChange={e => setFieldValue(e.target.value)} onBlur={saveField}>
                                            {editions.map(ed => <option key={ed} value={ed}>{ed}</option>)}
                                        </select>
                                        <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                                    </>
                                )
                                : (
                                    <>
                                        <span onClick={() => startEdit("edition", localMon.edition)}>{localMon.edition}</span>
                                        <span className="edit-icon" title="Edition ändern" onClick={() => startEdit("edition", localMon.edition)}>✏️</span>
                                    </>
                                )
                            }
                        </span>
                        <span className="owned-box">
                            {editField === "boxName" || editField === "box"
                                ? (
                                    <>
                                        <select value={fieldValue} onChange={e => setFieldValue(e.target.value)} onBlur={saveField}>
                                            {boxes.map(bx => <option key={bx} value={bx}>{bx}</option>)}
                                        </select>
                                        <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                                    </>
                                )
                                : (
                                    <>
                                        <span onClick={() => startEdit("boxName", localMon.boxName)}>{localMon.boxName}</span>
                                        <span className="edit-icon" title="Box ändern" onClick={() => startEdit("boxName", localMon.boxName)}>✏️</span>
                                    </>
                                )
                            }
                        </span>
                    </div>
                    <div className="owned-types" style={{ marginTop: 16 }}>
                        <span className={"poke-type " + localMon.type1.toLowerCase()}>{localMon.type1}</span>
                        {localMon.type2 && localMon.type2 !== "NORMAL" && localMon.type2 !== localMon.type1 ?
                            <span className={"poke-type " + localMon.type2.toLowerCase()}>{localMon.type2}</span> : null}
                    </div>
                </div>

                {/* Entwicklungsmöglichkeiten */}
                <div style={{ marginTop: 24 }}>
                    <h3>Mögliche Entwicklungen</h3>
                    {evolutionRules[localMon.pokedexId]?.length > 0 ? (
                        <div style={{ display: "flex", gap: 24, justifyContent: "center" }}>
                            {evolutionRules[localMon.pokedexId].map(targetId => {
                                const spriteNr = String(targetId).padStart(3, "0");
                                const targetSpecies = species.find(s => s.pokedexId === targetId);
                                return (
                                    <div key={targetId} style={{ textAlign: "center" }}>
                                        <img
                                            src={`/sprites/${spriteNr}.png`}
                                            alt={targetSpecies?.name || `#${targetId}`}
                                            style={{ width: 80, height: 80 }}
                                            onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                                        />
                                        <div style={{ fontWeight: 600, margin: "6px 0" }}>
                                            {targetSpecies?.name || `#${targetId}`}
                                        </div>
                                        <button
                                            className="add-btn"
                                            onClick={() => handleEvolve(targetId)}
                                            style={{ marginTop: 6 }}
                                            disabled={updating}
                                        >
                                            Entwickeln
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    ) : (
                        <div>Keine Entwicklung möglich</div>
                    )}
                </div>

                {error && <div style={{ color: "red", margin: "10px 0" }}>{error}</div>}
                <div style={{ marginTop: 30, display: "flex", gap: 16 }}>
                    <button className="add-btn" style={{ background: "#d21f27" }}
                            onClick={handleDelete} disabled={updating}>
                        Löschen
                    </button>
                    <button className="add-btn" style={{ background: "#888" }} onClick={onClose}>Schließen</button>
                </div>
            </div>
        </div>
    );
}