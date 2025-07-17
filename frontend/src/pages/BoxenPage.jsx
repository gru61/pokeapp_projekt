import { useState, useEffect } from "react";
import PokemonCard from "../components/PokemonCard";
import OrganizeView from "../components/OrganizeView";

export default function BoxenPage() {
    const [editions, setEditions] = useState([]);
    const [boxes, setBoxes] = useState([]);
    const [selectedEdition, setSelectedEdition] = useState("");
    const [selectedBox, setSelectedBox] = useState("");
    const [boxData, setBoxData] = useState(null);
    const [loadingBox, setLoadingBox] = useState(false);
    const [organizeMode, setOrganizeMode] = useState(false);

    // Hole Editionen und Boxen für Dropdowns
    useEffect(() => {
        const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

        fetch(`${API_URL}/api/boxes/editions`)
            .then(res => res.json())
            .then(data => {
                setEditions(data);
                if (data.length > 0) setSelectedEdition(data[0]);
            })
            .catch(err => console.error("Fehler beim Laden der Editionen:", err));

        fetch(`${API_URL}/api/boxes/names`)
            .then(res => res.json())
            .then(data => {
                setBoxes(data);
                if (data.length > 0) setSelectedBox(data[0]);
            })
            .catch(err => console.error("Fehler beim Laden der Boxnamen:", err));
    }, []);

    // Lade Box-Daten beim Wechsel
    useEffect(() => {
        if (selectedEdition && selectedBox) {
            setLoadingBox(true);
            fetch(`http://localhost:8080/api/boxes/${selectedEdition}/${selectedBox}`)
                .then(res => {
                    if (!res.ok) throw new Error("Box konnte nicht geladen werden!");
                    return res.json();
                })
                .then(setBoxData)
                .catch(() => setBoxData(null))
                .finally(() => setLoadingBox(false));
        }
    }, [selectedEdition, selectedBox]);

    // Berechne Maximalkapazität
    const maxCapacity = selectedBox === "Team" ? 6 : 20;
    const currentCount = boxData?.pokemons.length || 0;

    if (organizeMode) {
        return (
            <OrganizeView
                editions={editions}
                boxes={boxes}
                onBack={() => setOrganizeMode(false)}
            />
        );
    }

    return (
        <div className="boxenpage-container">
            {/* Box Navbar */}
            <div className="box-navbar">
                <select
                    value={selectedEdition}
                    onChange={(e) => setSelectedEdition(e.target.value)}
                >
                    {editions.map((ed) => (
                        <option key={ed} value={ed}>
                            {ed}
                        </option>
                    ))}
                </select>

                <select
                    value={selectedBox}
                    onChange={(e) => setSelectedBox(e.target.value)}
                >
                    {boxes.map((box) => (
                        <option key={box} value={box}>
                            {box}
                        </option>
                    ))}
                </select>

                <button
                    className="add-btn"
                    style={{ marginLeft: 28, background: "#ffb400", color: "#222" }}
                    onClick={() => setOrganizeMode(true)}
                >
                    Organisieren
                </button>
            </div>

            {/* Hauptansicht */}
            <div className="box-content">
                <h2 className="box-title">
                    Box: {selectedBox} | Edition: {selectedEdition}
                </h2>

                {loadingBox && <div>Lade Box...</div>}

                {!loadingBox && (!boxData || boxData.pokemons.length === 0) && (
                    <div className="no-pokemon">Keine Pokémon in dieser Box</div>
                )}

                {!loadingBox && boxData && boxData.pokemons.length > 0 && (
                    <>
                        <div className="box-capacity">
                            Kapazität: {boxData.pokemons.length} / {boxData.capacity}
                        </div>

                        <div className="pokemon-grid">
                            {boxData.pokemons.map((mon) => (
                                <PokemonCard key={mon.id} mon={mon} />
                            ))}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}