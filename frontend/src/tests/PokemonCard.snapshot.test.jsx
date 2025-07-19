import '@testing-library/jest-dom';
import { render } from "@testing-library/react";
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

test("Snapshot von PokemonCard", () => {
    const { asFragment } = render(<PokemonCard mon={mon} />);
    expect(asFragment()).toMatchSnapshot();
});
