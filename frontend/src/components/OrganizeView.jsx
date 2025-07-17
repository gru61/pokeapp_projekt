import { useState, useEffect } from "react";
import PokemonCard from "./PokemonCard";
import "../styles/OrganizeView.css";

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

    // Lade Daten für beide Boxen
    useEffect(() => {
        if (leftEdition && leftBox) {
            fetchData(`http://localhost:8080/api/boxes/${leftEdition}/${leftBox}`, setLeftData, setLoadingLeft);
        }
    }, [leftEdition, leftBox]);

    useEffect(() => {
        if (rightEdition && rightBox) {
            fetchData(`http://localhost:8080/api/boxes/${rightEdition}/${rightBox}`, setRightData, setLoadingRight);
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

    const movePokemon = async (targetBox, targetEdition) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/boxes/${draggedPokemon.boxName}/move-to/${targetBox}/${draggedPokemon.id}/${draggedPokemon.edition}/${targetEdition}`,
                {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                }
            );

            if (!response.ok) throw new Error("Verschieben fehlgeschlagen");

            // Aktualisiere beide Boxen
            fetchData(`http://localhost:8080/api/boxes/${leftEdition}/${leftBox}`, setLeftData, setLoadingLeft);
            fetchData(`http://localhost:8080/api/boxes/${rightEdition}/${rightBox}`, setRightData, setLoadingRight);
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