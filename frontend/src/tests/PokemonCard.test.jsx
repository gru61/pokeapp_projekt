import '@testing-library/jest-dom';
import { render, screen } from "@testing-library/react";
import PokemonCard from "../components/PokemonCard.jsx";


const mon = {
    id: 1,
    pokedexId: 25,
    speciesName: "Pikachu",
    nickname: "Pika",
    boxName: "Team",
    level: 7,
    edition: "Rot",
    type1: "ELEKTRO",
};

test("zeigt Nickname, Artname, Level und Typ", () => {
    render(<PokemonCard mon={mon} />);
    expect(screen.getByText("Pika")).toBeInTheDocument();
    expect(screen.getByText("Pikachu")).toBeInTheDocument();
    expect(screen.getByText(/Lvl\.\s*7/)).toBeInTheDocument();
    expect(screen.getByText("ELEKTRO")).toBeInTheDocument();
});
