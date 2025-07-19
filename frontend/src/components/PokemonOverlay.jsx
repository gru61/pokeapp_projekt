import { useState } from "react";

/**
 * @component PokemonOverlay
 * @description
 * Universelles Overlay für Detailansicht, Hinzufügen und Bearbeitung eines Pokémon.
 *
 * Je nach Modus ("add" oder "update") kann das Overlay entweder zum Erfassen eines neuen Pokémon
 * (inkl. Level, Edition, Box, Nickname) oder zur Anzeige und Bearbeitung eines bereits gefangenen Pokémon
 * (inkl. Entwicklung, Editieren, Löschen) genutzt werden.
 *
 * Props:
 * @param {Object} props.mon - Pokémon-Objekt (Arten- oder Besitzobjekt)
 * @param {Array<string>} props.editions - Liste der verfügbaren Editionen
 * @param {Array<string>} props.boxes - Liste der verfügbaren Boxnamen
 * @param {Object} [props.evolutionRules] - Mapping aller Entwicklungsregeln (für "update"-Modus)
 * @param {Array<Object>} [props.species] - Liste aller Pokémon-Arten (für Entwicklungsanzeige)
 * @param {"add"|"update"} [props.mode="update"] - Steuert Layout und Logik des Overlays
 * @param {Function} [props.onSave] - Wird beim Speichern im "add"-Modus aufgerufen
 * @param {Function} [props.onUpdate] - Wird beim Speichern im "update"-Modus aufgerufen
 * @param {Function} [props.onDelete] - Wird beim Löschen aufgerufen (nur im "update"-Modus)
 * @param {Function} [props.onEvolve] - Wird beim Entwickeln aufgerufen (nur im "update"-Modus)
 * @param {Function} props.onClose - Wird beim Schließen des Overlays aufgerufen (Pflicht)
 *
 * Besonderheiten:
 * - Zeigt Detailinformationen, Sprite, Typen, Nickname, Level, Edition und Box.
 * - Im "add"-Modus: Formular für neues Pokémon; Pflichtfelder validiert.
 * - Im "update"-Modus: Bearbeiten aller Felder, Entwickeln und Löschen möglich.
 * - Mögliche Entwicklungen werden (sofern vorhanden) mit Sprites und Button angezeigt.
 * - Fehlerzustände und Ladeindikatoren werden behandelt.
 *
 * Typische Verwendung:
 * - Als Overlay bei Klick auf eine Pokémon-Karte in Pokédex oder Besitz-Ansicht.
 * - Für Drag & Drop oder Entwicklung direkt aus der Detailansicht.
 *
 * @example
 * <PokemonOverlay
 *   mon={pokemon}
 *   editions={["Rot", "Blau"]}
 *   boxes={["Team", "Box 1"]}
 *   evolutionRules={{ 25: [26] }}
 *   species={[...]}
 *   mode="update"
 *   onUpdate={...}
 *   onDelete={...}
 *   onEvolve={...}
 *   onClose={() => setActiveMon(null)}
 * />
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
                                           onClose
                                       }) {
    // --- Gemeinsame States ---
    const [form, setForm] = useState({
        nickname: "",
        level: 1,
        edition: "",
        box: ""
    });
    const [editField, setEditField] = useState(null);
    const [fieldValue, setFieldValue] = useState("");
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState(null);
    const [localMon, setLocalMon] = useState(mon);

    const evolutions = evolutionRules[localMon.pokedexId] || [];
    const spriteNr = String(mon.pokedexId).padStart(3, "0");
    const editionClass = (mode === "update" ? localMon.edition : form.edition)
        ? "edition-" + (mode === "update" ? localMon.edition : form.edition).toLowerCase()
        : "";

    // --- Handler für ADD-Dialog (Pokedex) ---
    const handleAdd = async () => {
        if (!form.level || !form.edition || !form.box) {
            setError("Alle Pflichtfelder ausfüllen!");
            return;
        }
        if (form.nickname && form.nickname.length > 11) {
            setError("Nickname max. 11 Zeichen!");
            return;
        }
        setError(null);
        setUpdating(true);
        try {
            await onSave({
                pokedexId: mon.pokedexId,
                nickname: form.nickname.trim(),
                level: Number(form.level),
                edition: form.edition,
                box: form.box
            });
            onClose();
        } catch (err) {
            setError(err.message || "Fehler beim Hinzufügen.");
        } finally {
            setUpdating(false);
        }
    };

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

        let updateData = {
            pokedexId: localMon.pokedexId,
            nickname: localMon.nickname,
            level: localMon.level,
            edition: localMon.edition,
            box: localMon.box || localMon.boxName
        };
        updateData[editField === "boxName" ? "box" : editField] = fieldValue;

        try {
            await onUpdate({
                id: localMon.id,
                ...updateData
            });
            setLocalMon({ ...localMon, [editField]: fieldValue, ...(editField === "box" && { boxName: fieldValue }) });
            setEditField(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    };

    // --- Handler für EVOLVE (OwnedPage) ---
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

            const updatedMon = await resp.json();
            setLocalMon(updatedMon);

            // Informiere Elternkomponente und/oder schließe das Overlay
            if (typeof onEvolve === "function") onEvolve(updatedMon); // <- NEU
            onClose(); // <- NEU

        } catch (err) {
            setError(err.message);
        } finally {
            setUpdating(false);
        }
    };


    // --- Layout ---

    if (mode === "add") {
        // --- POKEDEX/FANG OVERLAY ---
        return (
            <div className="overlay-bg" onClick={onClose}>
                <div className={`overlay-card ${editionClass}`} onClick={e => e.stopPropagation()}>
                    {/* Oben rechts: Pokedex-ID */}
                    <div className="pokemon-speciesid" style={{ right: 22, top: 22, fontSize: "1.08rem" }}>
                        #{spriteNr}
                    </div>
                    {/* Sprite */}
                    <div className="overlay-sprite-bg">
                        <img
                            src={`/sprites/${spriteNr}.png`}
                            alt={mon.name}
                            className="overlay-sprite"
                            onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                        />
                    </div>
                    {/* Typen */}
                    <div className="pokemon-types" style={{ margin: "12px 0 18px 0" }}>
                        <span className={`poke-type ${mon.type1.toLowerCase()}`}>{mon.type1}</span>
                        {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 &&
                            <span className={`poke-type ${mon.type2.toLowerCase()}`}>{mon.type2}</span>
                        }
                    </div>
                    {/* Button & Formular */}
                    <div className="overlay-fields" style={{ marginTop: 0 }}>
                        <label>
                            Nickname (optional)
                            <input
                                type="text"
                                maxLength={11}
                                value={form.nickname}
                                onChange={e => setForm(f => ({ ...f, nickname: e.target.value }))}
                                disabled={updating}
                            />
                        </label>
                        <label>
                            Level
                            <select
                                value={form.level}
                                onChange={e => setForm(f => ({ ...f, level: e.target.value }))}
                                disabled={updating}
                            >
                                {Array.from({ length: 100 }, (_, i) => i + 1).map(i =>
                                    <option key={i} value={i}>{i}</option>
                                )}
                            </select>
                        </label>
                        <label>
                            Edition
                            <select
                                value={form.edition}
                                onChange={e => setForm(f => ({ ...f, edition: e.target.value }))}
                                required
                            >
                                <option value="">Bitte wählen…</option>
                                {editions.map(ed =>
                                    <option key={ed} value={ed}>{ed}</option>
                                )}
                            </select>
                        </label>

                        <label>
                            Box
                            <select
                                value={form.box}
                                onChange={e => setForm(f => ({ ...f, box: e.target.value }))}
                                required
                            >
                                <option value="">Bitte wählen…</option>
                                {boxes.map(bx =>
                                    <option key={bx} value={bx}>{bx}</option>
                                )}
                            </select>
                        </label>
                    </div>
                    <button
                        className="add-btn"
                        onClick={handleAdd}
                        disabled={updating}
                        style={{ minWidth: 140, fontSize: "1.12rem", margin: "18px auto 0 auto", display: "block" }}
                    >
                        Hinzufügen
                    </button>
                    {error && <div className="error-message" style={{ marginTop: 10 }}>{error}</div>}
                </div>
            </div>
        );
    }

    // --- OWNED PAGE / BESITZ-OVERLAY ---
    const showNickname = localMon.nickname && localMon.nickname.trim().length > 0;

    return (
        <div className="overlay-bg" onClick={onClose}>
            <div className={`overlay-card ${editionClass}`} onClick={e => e.stopPropagation()}>
                {/* Oben rechts: Pokedex-ID */}
                <div className="pokemon-speciesid" style={{ right: 18, top: 18, fontSize: "1.08rem" }}>
                    #{spriteNr}
                </div>
                {/* Oben links: Edition & Box */}
                <div style={{ position: "absolute", top: 18, left: 18, textAlign: "left", lineHeight: 1.25 }}>
                    <div className="pokemon-edition">{localMon.edition}</div>
                    <div className="pokemon-box">{localMon.box || localMon.boxName}</div>
                </div>
                {/* Sprite */}
                <div className="overlay-sprite-bg">
                    <img
                        src={`/sprites/${spriteNr}.png`}
                        alt={localMon.speciesName}
                        className="overlay-sprite"
                        onError={e => { e.target.onerror = null; e.target.src = "/sprites/placeholder.png"; }}
                    />
                </div>
                {/* Name/Nickname */}
                <div className="pokemon-name" style={{ fontSize: "1.27rem" }}>
                    {showNickname ? (
                        <>
                            <span className="pokemon-nickname">{localMon.nickname}</span>
                            <span className="pokemon-realname">{localMon.speciesName}</span>
                        </>
                    ) : (
                        <span>{localMon.speciesName}</span>
                    )}
                </div>
                {/* Typen */}
                <div className="pokemon-types">
                    <span className={`poke-type ${mon.type1?.toLowerCase()}`}>{mon.type1}</span>
                    {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 && (
                        <span className={`poke-type ${mon.type2.toLowerCase()}`}>{mon.type2}</span>
                    )}
                </div>
                {/* Level */}
                <div className="pokemon-level-inline" style={{ marginBottom: 14 }}>
                    Lvl. {localMon.level}
                </div>
                {/* Aktionen */}
                <div style={{ display: "flex", gap: 14, justifyContent: "center", marginBottom: 18 }}>
                    <button
                        className="add-btn"
                        style={{ minWidth: 100 }}
                        onClick={() => setEditField("update")}
                    >
                        Aktualisieren
                    </button>
                    <button
                        className="add-btn danger"
                        style={{ minWidth: 100 }}
                        onClick={onDelete}
                        disabled={updating}
                    >
                        Löschen
                    </button>
                </div>

                {/* UPDATE-Formular als Overlay oder Inline */}
                {editField === "update" && (
                    <div className="overlay-fields" style={{ marginBottom: 10 }}>
                        {/* Nickname */}
                        <label>
                            Nickname
                            <input
                                maxLength={11}
                                value={localMon.nickname || ""}
                                onChange={e => setLocalMon(m => ({ ...m, nickname: e.target.value }))}
                                onBlur={() => saveFieldWrapper("nickname", localMon.nickname)}
                                disabled={updating}
                            />
                        </label>
                        {/* Level */}
                        <label>
                            Level
                            <select
                                value={localMon.level}
                                onChange={e => setLocalMon(m => ({ ...m, level: Number(e.target.value) }))}
                                onBlur={() => saveFieldWrapper("level", localMon.level)}
                                disabled={updating}
                            >
                                {Array.from({ length: 100 }, (_, i) => i + 1).map(i =>
                                    <option key={i} value={i}>{i}</option>
                                )}
                            </select>
                        </label>
                        {/* Edition */}
                        <label>
                            Edition
                            <select
                                value={localMon.edition}
                                onChange={e => setLocalMon(m => ({ ...m, edition: e.target.value }))}
                                onBlur={() => saveFieldWrapper("edition", localMon.edition)}
                                disabled={updating}
                            >
                                {editions.map(ed => (
                                    <option key={ed} value={ed}>{ed}</option>
                                ))}
                            </select>
                        </label>
                        {/* Box */}
                        <label>
                            Box
                            <select
                                value={localMon.box || localMon.boxName}
                                onChange={e => setLocalMon(m => ({ ...m, box: e.target.value }))}
                                onBlur={() => saveFieldWrapper("box", localMon.box || localMon.boxName)}
                                disabled={updating}
                            >
                                {boxes.map(bx => (
                                    <option key={bx} value={bx}>{bx}</option>
                                ))}
                            </select>
                        </label>
                        <button
                            className="add-btn"
                            style={{ minWidth: 120, marginTop: 14 }}
                            onClick={() => {
                                setEditField(null);
                                saveField();
                            }}
                            disabled={updating}
                        >Speichern</button>
                    </div>
                )}

                {/* Entwicklung (nur wenn möglich, darunter Button „Entwickeln“) */}
                <div style={{ marginTop: 18 }}>
                    <h3 style={{ marginBottom: 8 }}>Mögliche Entwicklungen</h3>
                    {evolutions.length > 0 ? (
                        <div style={{ display: "flex", gap: 24, justifyContent: "center" }}>
                            {evolutions.map(targetId => {
                                const targetSpecies = species.find(s => s.pokedexId === targetId);
                                const evoSpriteNr = String(targetId).padStart(3, "0");
                                return (
                                    <div key={targetId} style={{ textAlign: "center" }}>
                                        <img
                                            src={`/sprites/${evoSpriteNr}.png`}
                                            alt={targetSpecies?.name || `#${targetId}`}
                                            className="overlay-evo-sprite"
                                        />
                                        <div style={{ fontWeight: 600, margin: "6px 0" }}>
                                            {targetSpecies?.name || `#${targetId}`}
                                        </div>
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
                        <div>Keine Entwicklung möglich</div>
                    )}
                </div>
                {error && <div className="error-message">{error}</div>}
            </div>
        </div>
    );

    // Helper für das Inline-Update-Formular
    function saveFieldWrapper(field, value) {
        setEditField(field);
        setFieldValue(value);
        saveField();
    }
}
