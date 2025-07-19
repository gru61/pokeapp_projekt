import '@testing-library/jest-dom';
import { render, screen } from "@testing-library/react";
import PokemonOverlay from "../components/PokemonOverlay.jsx";

const mon = {
    pokedexId: 1,
    name: "Bisasam",
    type1: "PFLANZE",
};

test("zeigt Pflichtfelder im Add-Dialog an", () => {
    render(
        <PokemonOverlay
            mon={mon}
            editions={["Rot", "Blau"]}
            boxes={["Team", "Box 1"]}
            mode="add"
            onSave={() => {}}
            onClose={() => {}}
        />
    );
    expect(screen.getByLabelText(/Level/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Edition/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Box/i)).toBeInTheDocument();
});
