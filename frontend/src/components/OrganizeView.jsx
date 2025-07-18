import { useState, useEffect } from "react";
import PokemonCard from "./PokemonCard";
import "../styles/OrganizeView.css";

// --- Helper für API-Konvertierung (Enum, BoxName) ---
function apiEnum(val) {
    if (!val) return "";
    return val
        .toUpperCase()
        .replace("Ü", "UE")
        .replace("Ö", "OE")
        .replace("Ä", "AE")
        .replace("ß", "SS");
}

function apiBoxName(name) {
    if (!name) return "";
    if (name === "Team") return "TEAM";
    const boxMatch = name.match(/^Box (\d{1,2})$/);
    if (boxMatch) {
        return "BOX" + boxMatch[1];
    }
    // Fallback: alles groß, Leerzeichen raus
    return name.toUpperCase().replace(/\s+/g, "");
}


/**
 * @component OrganizeView
 * @description
 * Zwei-Panel-Ansicht zur Organisation und zum Drag & Drop-Verschieben von Pokémon zwischen verschiedenen Boxen und Editionen.
 *
 * Nutzer können aus Dropdowns Edition und Box für linkes sowie rechtes Panel wählen und Pokémon per Drag & Drop zwischen den Panels verschieben.
 * Die Komponente sorgt für API-Mapping (Enum-Konvertierung für die Backend-Kommunikation) und behandelt Fehlerzustände (Laden, Verschieben, etc.).
 *
 * Props:
 * @param {string[]} editions - Liste der verfügbaren Editionen (z.B. ["Rot", "Blau", ...]).
 * @param {string[]} boxes - Liste der verfügbaren Boxnamen (z.B. ["Team", "Box 1", ...]).
 * @param {function} onBack - Callback, um zur vorherigen Ansicht zurückzukehren.
 *
 * Besonderheiten:
 * - Nutzt interne Helper (apiEnum, apiBoxName) zur Umwandlung der UI-Strings in die erwarteten API-Enumwerte.
 * - Lädt automatisch beim Wechsel der Auswahl jeweils die Pokémon der gewählten Box/Edition.
 * - Drag & Drop ist zwischen beiden Panels möglich. Die Verschiebung löst einen API-Call aus und aktualisiert anschließend beide Seiten.
 * - Fehler und Ladezustände werden je Panel separat behandelt.
 *
 * Typische Verwendung:
 * - Wird im Boxen-/Organize-Modus angezeigt, wenn der Nutzer Pokémon gezielt zwischen Boxen/Editionen umsortieren möchte.
 * - Im UI eingebunden z.B. via <OrganizeView editions={...} boxes={...} onBack={...} />
 *
 * @example
 * <OrganizeView editions={["Rot", "Blau"]} boxes={["Team", "Box 1"]} onBack={() => navigate(-1)} />
 */

export default function OrganizeView({ editions, boxes, onBack }) {
    const [leftEdition, setLeftEdition] = useState(editions[0] || "");
    const [leftBox, setLeftBox] = useState(boxes[0] || "");
    const [rightEdition, setRightEdition] = useState(editions[1] || editions[0] || "");
    const [rightBox, setRightBox] = useState(boxes[0] || "");

    const [leftData, setLeftData] = useState(null);
    const [rightData, setRightData] = useState(null);

    const [loadingLeft, setLoadingLeft] = useState(false);
    const [loadingRight, setLoadingRight] = useState(false);
    const [error, setError] = useState(null);

    const [draggedPokemon, setDraggedPokemon] = useState(null);
    const [isDraggingOverLeft, setIsDraggingOverLeft] = useState(false);
    const [isDraggingOverRight, setIsDraggingOverRight] = useState(false);

    // Lade Daten für beide Boxen (mit Mapping!)
    useEffect(() => {
        if (leftEdition && leftBox) {
            fetchData(
                `http://localhost:8080/api/boxes/${apiEnum(leftEdition)}/${apiBoxName(leftBox)}`,
                setLeftData,
                setLoadingLeft
            );
        }
    }, [leftEdition, leftBox]);

    useEffect(() => {
        if (rightEdition && rightBox) {
            fetchData(
                `http://localhost:8080/api/boxes/${apiEnum(rightEdition)}/${apiBoxName(rightBox)}`,
                setRightData,
                setLoadingRight
            );
        }
    }, [rightEdition, rightBox]);

    const fetchData = async (url, setData, setLoadingState) => {
        setLoadingState(true);
        try {
            const res = await fetch(url);
            if (!res.ok) throw new Error("Fehler beim Laden");
            setData(await res.json());
        } catch (err) {
            setError("Fehler beim Laden: " + err.message);
        } finally {
            setLoadingState(false);
        }
    };

    const handleDragStart = (mon) => {
        setDraggedPokemon(mon);
    };

    const handleDragEnd = () => {
        setDraggedPokemon(null);
        setIsDraggingOverLeft(false);
        setIsDraggingOverRight(false);
    };

    // --- Move mit korrektem Enum-Mapping! ---
    const movePokemon = async (targetBoxDisplay, targetEditionDisplay) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/boxes/${apiBoxName(draggedPokemon.boxName)}/move-to/` +
                `${apiBoxName(targetBoxDisplay)}/${draggedPokemon.id}/${apiEnum(draggedPokemon.edition)}/${apiEnum(targetEditionDisplay)}`,
                {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                }
            );

            if (!response.ok) throw new Error("Verschieben fehlgeschlagen");

            // Aktualisiere beide Boxen (mit Mapping!)
            fetchData(
                `http://localhost:8080/api/boxes/${apiEnum(leftEdition)}/${apiBoxName(leftBox)}`,
                setLeftData,
                setLoadingLeft
            );
            fetchData(
                `http://localhost:8080/api/boxes/${apiEnum(rightEdition)}/${apiBoxName(rightBox)}`,
                setRightData,
                setLoadingRight
            );
        } catch (err) {
            setError("Fehler beim Verschieben: " + err.message);
        } finally {
            handleDragEnd();
        }
    };

    const handleDrop = (side) => {
        if (!draggedPokemon) return;

        if (side === "left") {
            movePokemon(leftBox, leftEdition);
        } else {
            movePokemon(rightBox, rightEdition);
        }
    };

    return (
        <div className="organize-container">
            {/* Linke Box */}
            <div
                className={`organize-panel ${isDraggingOverLeft ? "drag-over" : ""}`}
                onDragOver={(e) => {
                    e.preventDefault();
                    setIsDraggingOverLeft(true);
                }}
                onDragLeave={() => setIsDraggingOverLeft(false)}
                onDrop={() => handleDrop("left")}
            >
                <h3>Box links</h3>
                <div className="organize-controls">
                    <select value={leftEdition} onChange={(e) => setLeftEdition(e.target.value)}>
                        {editions.map((ed) => (
                            <option key={ed} value={ed}>
                                {ed}
                            </option>
                        ))}
                    </select>
                    <select value={leftBox} onChange={(e) => setLeftBox(e.target.value)}>
                        {boxes.map((box) => (
                            <option key={box} value={box}>
                                {box}
                            </option>
                        ))}
                    </select>
                </div>

                {loadingLeft ? (
                    <div className="loading">Lade…</div>
                ) : error ? (
                    <div className="error">{error}</div>
                ) : (
                    <div className="pokemon-grid">
                        {leftData?.pokemons.map((mon) => (
                            <div
                                key={mon.id}
                                draggable
                                onDragStart={() => handleDragStart(mon)}
                                onDragEnd={handleDragEnd}
                                className="draggable-card"
                            >
                                <PokemonCard mon={mon} />
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Rechte Box */}
            <div
                className={`organize-panel ${isDraggingOverRight ? "drag-over" : ""}`}
                onDragOver={(e) => {
                    e.preventDefault();
                    setIsDraggingOverRight(true);
                }}
                onDragLeave={() => setIsDraggingOverRight(false)}
                onDrop={() => handleDrop("right")}
            >
                <h3>Box rechts</h3>
                <div className="organize-controls">
                    <select value={rightEdition} onChange={(e) => setRightEdition(e.target.value)}>
                        {editions.map((ed) => (
                            <option key={ed} value={ed}>
                                {ed}
                            </option>
                        ))}
                    </select>
                    <select value={rightBox} onChange={(e) => setRightBox(e.target.value)}>
                        {boxes.map((box) => (
                            <option key={box} value={box}>
                                {box}
                            </option>
                        ))}
                    </select>
                </div>

                {loadingRight ? (
                    <div className="loading">Lade…</div>
                ) : error ? (
                    <div className="error">{error}</div>
                ) : (
                    <div className="pokemon-grid">
                        {rightData?.pokemons.map((mon) => (
                            <div
                                key={mon.id}
                                draggable
                                onDragStart={() => handleDragStart(mon)}
                                onDragEnd={handleDragEnd}
                                className="draggable-card"
                            >
                                <PokemonCard mon={mon} />
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Zurück-Button */}
            <button className="add-btn back-btn" onClick={onBack}>
                Zurück
            </button>
        </div>
    );
}
