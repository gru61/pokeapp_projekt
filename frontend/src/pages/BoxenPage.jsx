import { useEffect, useState } from "react";

export default function BoxesPage() {
    const [editions, setEditions] = useState([]);
    const [boxNames, setBoxNames] = useState([]);
    const [selectedEdition, setSelectedEdition] = useState("");
    const [selectedBox, setSelectedBox] = useState("");
    const [box, setBox] = useState(null);
    const [loadingBox, setLoadingBox] = useState(false);

    // Lade Editions-Enum
    useEffect(() => {
        fetch("http://localhost:8080/api/editions")
            .then(res => res.json())
            .then(data => {
                setEditions(data);
                if (data.length > 0) setSelectedEdition(data[0]);
            });
    }, []);

// Lade BoxName-Enum
    useEffect(() => {
        fetch("http://localhost:8080/api/boxnames")
            .then(res => res.json())
            .then(data => {
                setBoxNames(data);
                if (data.length > 0) setSelectedBox(data[0]);
            });
    }, []);

    // Lade Boxdaten immer bei Änderung
    useEffect(() => {
        if (selectedEdition && selectedBox) {
            setLoadingBox(true);
            fetch(`http://localhost:8080/api/boxes/${selectedEdition}/${selectedBox}`)
                .then(res => {
                    if (!res.ok) throw new Error("Box konnte nicht geladen werden!");
                    return res.json();
                })
                .then(setBox)
                .catch(() => setBox(null))
                .finally(() => setLoadingBox(false));
        }
    }, [selectedEdition, selectedBox]);

    return (
        <div>
            {/* BoxNavbar */}
            <div style={{
                display: "flex",
                gap: "18px",
                padding: "20px 32px 10px 32px",
                background: "#1f4068",
                borderBottom: "1px solid #283655",
                alignItems: "center",
                justifyContent: "center"
            }}>
                <select
                    value={selectedEdition}
                    onChange={e => setSelectedEdition(e.target.value)}
                    style={{
                        fontSize: "1.1rem",
                        padding: "6px 14px",
                        borderRadius: "10px",
                        border: "1px solid #283655",
                        background: "#fff",
                        color: "#162447",
                        fontWeight: 500,
                        minWidth: 130
                    }}
                >
                    {editions.map(edition =>
                        <option key={edition} value={edition}>{edition}</option>
                    )}
                </select>
                <select
                    value={selectedBox}
                    onChange={e => setSelectedBox(e.target.value)}
                    style={{
                        fontSize: "1.1rem",
                        padding: "6px 14px",
                        borderRadius: "10px",
                        border: "1px solid #283655",
                        background: "#fff",
                        color: "#162447",
                        fontWeight: 500,
                        minWidth: 130
                    }}
                >
                    {boxNames.map(box =>
                        <option key={box} value={box}>{box}</option>
                    )}
                </select>
            </div>

            {/* Box-Inhalt */}
            <div style={{ color: "#f8f8f8", textAlign: "center", marginTop: 32 }}>
                <h2>Box: {selectedBox} | Edition: {selectedEdition}</h2>
                {loadingBox && <div>Lade Box...</div>}
                {!loadingBox && box && (
                    <>
                        <div style={{ color: "#ffb400", margin: "8px" }}>
                            Kapazität: {box.pokemons.length} / {box.capacity}
                        </div>
                        <div className="owned-grid" style={{ marginTop: 24 }}>
                            {box.pokemons.map(mon => {
                                const spriteNr = String(mon.pokedexId).padStart(3, "0");
                                const showNickname = mon.nickname && mon.nickname.trim().length > 0;
                                return (
                                    <div key={mon.id} className="owned-card" tabIndex={0}>
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
                    </>
                )}
                {!loadingBox && box && box.pokemons.length === 0 && (
                    <div style={{ margin: "32px", color: "#888" }}>Keine Pokémon in dieser Box.</div>
                )}
            </div>
        </div>
    );
}
