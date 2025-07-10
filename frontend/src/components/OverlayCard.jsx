import React, {useState} from 'react';
import {addOwnedPokemon, updateOwnedPokemon} from "../api/pokemonApi";

export default function OverlayCard({species, onClose, onSave, ownedData = null}) {
    const [nickname, setNickname] = useState(ownedData?.nickname || "");
    const [level, setLevel] = useState(ownedData?.level || 50);
    const [edition, setEdition] = useState(ownedData?.edition || "NORMAL");
    consr [boxId, setBoxId] = useState(ownedData?.boxId || 1);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = {
            speciesId: species.pokedexId,
            nickname: nickname || undefined,
            level: parseInt(level),
            edition,
            boxId: parseInt(boxId)
        };

        let result;
        if (ownedData) {
            result = await updateOwnedPokemon(ownedData.id, payload);
        } else {
            result = await addOwnedPokemon(payload);
        }
        onSave(result);
    };

    return (
        <div className={"overlay"}>
            <div className={"overlay-content"}>
                <button className={"close-button"} onClick={onClose}>X</button>
                <h2>{species.name}</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        placeholder="Nickname"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                    />
                    <select value={level} onChange={(e) => setLevel(e.target.value)}>
                        {Array.from({length: 100}, (_, i) => (
                            <option key ={i+1} value={i+1}>{i+1}</option>
                            ))}
                    </select>
                    <div>
                        <label><input type="radio" name="edition" value="ROT" checked={edition === "ROT"} onChange={() => setEdition("ROT")}/>ROT</label>
                        <label><input type="radio" name="edition" value="BLAU" checked={edition === "BLAU"} onChange={() => setEdition("BLAU")}/>ROT</label>
                        <label><input type="radio" name="edition" value="GELB" checked={edition === "GELB"} onChange={() => setEdition("GELB")}/>ROT</label>
                        <label><input type="radio" name="edition" value="GRÜN" checked={edition === "GRÜN"} onChange={() => setEdition("GRÜN")}/>ROT</label>
                    </div>
                    <div>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                        <label htmlFor=""></label>
                    </div>
                    <button type ="submit">{ownedData? "Update" : "Hinzufügen"}</button>
                </form>
            </div>
        </div>
    )
}