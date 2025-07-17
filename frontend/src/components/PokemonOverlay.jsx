import { useState } from "react";

/**
 * @component PokemonOverlay
 * @description Overlay für Detailansicht, Bearbeiten und Entwickeln von Pokémon.
 * PATCH schickt immer alle Felder wie im funktionierenden Alt-Code.
 */
export default function PokemonOverlay({
                                           mon,
                                           editions,
                                           boxes,
                                           evolutionRules = {},
                                           species = [],
                                           mode = "update",
                                           onSave,
                                           onUpdate,
                                           onDelete,
                                           onEvolve,
                                           onClose,
                                           reloadList
                                       }) {
    const [editField, setEditField] = useState(null);
    const [fieldValue, setFieldValue] = useState("");
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState(null);
    const [localMon, setLocalMon] = useState(mon);

    const evolutions = evolutionRules[localMon.pokedexId] || [];
    const spriteNr = String(localMon.pokedexId).padStart(3, "0");
    const editionClass = localMon.edition ? "edition-" + localMon.edition.toLowerCase() : "";

    // PATCH-Objekt wie im Altcode: immer alle Felder
    const saveField = async () => {
        if (editField === "nickname" && fieldValue.length > 11) {
            setError("Nickname max. 11 Zeichen!");
            return;
        }
        if (editField === "level" && Number(fieldValue) < localMon.level) {
            setError("Level kann nicht gesenkt werden!");
            return;
        }

        setUpdating(true);
        setError(null);

        // Nimm alle aktuellen Felder, überschreibe das editierte
        let updateData = {
            pokedexId: localMon.pokedexId,
            nickname: localMon.nickname,
            level: localMon.level,
            edition: localMon.edition,
            box: localMon.box || localMon.boxName
        };
        updateData[editField === "boxName" ? "box" : editField] = fieldValue;

        console.log("PATCH UPDATE", updateData);

        try {
            const resp = await fetch(`http://localhost:8080/api/pokemon/${localMon.id}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updateData)
            });
            if (!resp.ok) throw new Error("Update fehlgeschlagen");
            setLocalMon({ ...localMon, [editField]: fieldValue, ...(editField === "box" && { boxName: fieldValue }) });
            setEditField(null);
            if (typeof reloadList === "function") reloadList();
        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    };

    // Entwicklung PATCH wie gehabt, aber immer alle Felder
    const handleEvolve = async (targetId) => {
        setUpdating(true);
        setError(null);
        const body = {
            pokedexId: targetId,
            nickname: localMon.nickname,
            level: localMon.level,
            edition: localMon.edition,
            box: localMon.box || localMon.boxName
        };
        try {
            const resp = await fetch(`http://localhost:8080/api/pokemon/${localMon.id}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body)
            });
            if (!resp.ok) throw new Error("Entwicklung fehlgeschlagen");
            onClose();
            if (typeof reloadList === "function") reloadList();
        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    };

    // Löschen
    const handleDelete = async () => {
        if (!window.confirm("Dieses Pokémon wirklich löschen?")) return;
        setUpdating(true);
        try {
            await onDelete();
            onClose();
            if (typeof reloadList === "function") reloadList();
        } finally {
            setUpdating(false);
        }
    };

    const startEdit = (field, value) => {
        setEditField(field);
        setFieldValue(value);
    };

    return (
        <div className="overlay-bg" onClick={onClose}>
            <div className={`overlay-card ${editionClass}`} onClick={e => e.stopPropagation()}>
                <div className="pokemon-speciesid">#{spriteNr}</div>
                <div className="overlay-sprite-bg">
                    <img
                        src={`/sprites/${spriteNr}.png`}
                        alt={localMon.speciesName}
                        className="overlay-sprite"
                        onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                    />
                </div>

                <div className="overlay-fields">
                    {/* Nickname */}
                    <label>
                        Nickname
                        {editField === "nickname" ? (
                            <div className="edit-field-group">
                                <input
                                    autoFocus
                                    maxLength={11}
                                    value={fieldValue}
                                    onChange={e => setFieldValue(e.target.value)}
                                    onBlur={saveField}
                                    onKeyDown={e => e.key === "Enter" && saveField()}
                                />
                                <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                            </div>
                        ) : (
                            <span className="editable" onClick={() => startEdit("nickname", localMon.nickname)}>
                                {localMon.nickname || <span className="placeholder">Kein Nickname</span>}
                                <span className="edit-icon" title="Nickname ändern"> ✏️</span>
                            </span>
                        )}
                    </label>

                    {/* Level */}
                    <label>
                        Level
                        {editField === "level" ? (
                            <div className="edit-field-group">
                                <select
                                    value={fieldValue}
                                    onChange={e => setFieldValue(Number(e.target.value))}
                                    onBlur={saveField}
                                    disabled={updating}
                                >
                                    {Array.from({ length: 100 }, (_, i) => i + 1).map(i =>
                                        <option key={i} value={i}>{i}</option>
                                    )}
                                </select>
                                <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                            </div>
                        ) : (
                            <span className="editable" onClick={() => startEdit("level", localMon.level)}>
                                Lvl. {localMon.level}
                                <span className="edit-icon" title="Level ändern"> ✏️</span>
                            </span>
                        )}
                    </label>

                    {/* Edition */}
                    <label>
                        Edition
                        {editField === "edition" ? (
                            <div className="edit-field-group">
                                <select
                                    value={fieldValue}
                                    onChange={e => setFieldValue(e.target.value)}
                                    onBlur={saveField}
                                    disabled={updating}
                                >
                                    {editions.map(ed => (
                                        <option key={ed} value={ed}>{ed}</option>
                                    ))}
                                </select>
                                <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                            </div>
                        ) : (
                            <span className="editable" onClick={() => startEdit("edition", localMon.edition)}>
                                {localMon.edition}
                                <span className="edit-icon" title="Edition ändern"> ✏️</span>
                            </span>
                        )}
                    </label>

                    {/* Box */}
                    <label>
                        Box
                        {editField === "box" || editField === "boxName" ? (
                            <div className="edit-field-group">
                                <select
                                    value={fieldValue}
                                    onChange={e => setFieldValue(e.target.value)}
                                    onBlur={saveField}
                                    disabled={updating}
                                >
                                    {boxes.map(bx => (
                                        <option key={bx} value={bx}>{bx}</option>
                                    ))}
                                </select>
                                <button className="add-btn" onClick={saveField} disabled={updating}>✔</button>
                            </div>
                        ) : (
                            <span className="editable" onClick={() => startEdit("box", localMon.box || localMon.boxName)}>
                                {localMon.box || localMon.boxName}
                                <span className="edit-icon" title="Box ändern"> ✏️</span>
                            </span>
                        )}
                    </label>
                </div>

                {/* Typen */}
                <div className="pokemon-types">
                    <span className={`poke-type ${mon.type1?.toLowerCase()}`}>{mon.type1}</span>
                    {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 && (
                        <span className={`poke-type ${mon.type2.toLowerCase()}`}>{mon.type2}</span>
                    )}
                </div>

                {/* Entwicklungsmöglichkeiten */}
                <div className="evolution-section">
                    <h3>Mögliche Entwicklungen</h3>
                    {evolutions.length > 0 ? (
                        <div className="evolution-grid">
                            {evolutions.map(targetId => {
                                const targetSpecies = species.find(s => s.pokedexId === targetId);
                                const evoSpriteNr = String(targetId).padStart(3, "0");
                                return (
                                    <div key={targetId} className="evolution-card">
                                        <img
                                            src={`/sprites/${evoSpriteNr}.png`}
                                            alt={targetSpecies?.name || `#${targetId}`}
                                            className="overlay-evo-sprite"
                                        />
                                        <div className="evolution-name">{targetSpecies?.name || `#${targetId}`}</div>
                                        <button
                                            className="add-btn"
                                            onClick={() => handleEvolve(targetId)}
                                            disabled={updating}
                                        >
                                            Entwickeln
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    ) : (
                        <div className="no-evolution">Keine Entwicklung möglich</div>
                    )}
                </div>

                {error && <div className="error-message">{error}</div>}

                <div className="overlay-actions">
                    <button className="add-btn danger" onClick={handleDelete} disabled={updating}>
                        Löschen
                    </button>
                    <button className="add-btn secondary" onClick={onClose}>
                        Schließen
                    </button>
                </div>
            </div>
        </div>
    );
}
