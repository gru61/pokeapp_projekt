import { useEffect, useState } from "react";
import PokemonCard from "../components/PokemonCard";
import "./Pokedex.css";

function Pokedex() {
    const [pokemonList, setPokemonList] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch("http://localhost:8080/api/species")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Fehler beim Laden der Pokémon-Daten");
                }
                return response.json();
            })
            .then((data) => {
                setPokemonList(data);
                setLoading(false);
            })
            .catch((error) => {
                console.error("Fetch-Fehler:", error);
                setLoading(false);
            });
    }, []);

    if (loading) {
        return <p>Pokédex wird geladen...</p>;
    }

    return (
        <div className="pokedex-page">
            <h1 className="pokedex-title">Pokédex Übersicht</h1>
            <div className="pokedex-grid">
                {pokemonList.map((pokemon) => (
                    <PokemonCard
                        key={pokemon.id}
                        id={pokemon.id}
                        name={pokemon.name}
                    />
                ))}
            </div>
        </div>
    );
}

export default Pokedex;