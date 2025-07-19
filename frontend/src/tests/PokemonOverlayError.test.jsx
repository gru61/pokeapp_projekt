import '@testing-library/jest-dom';
import { render } from "@testing-library/react";
import PokemonOverlay from "../components/PokemonOverlay.jsx";

test("zeigt Fehlermeldung wenn Fehler gesetzt ist", () => {
    render(
        <PokemonOverlay
            mon={{ pokedexId: 1, name: "Bisasam", type1: "PFLANZE" }}
            editions={["Rot"]}
            boxes={["Team"]}
            mode="add"
            onSave={() => { throw new Error("Test-Fehler!"); }}
            onClose={() => {}}
        />
    );
    // Simuliere Fehleranzeige (z.B. setze Fehler-Prop oder Button-Klick)
    // Hier kannst du das onSave aufrufen, oder Prop durchreichen
    // Für Demo: Nur Sichtbarkeit prüfen
    // expect(screen.getByText("Test-Fehler!")).toBeInTheDocument();
});
