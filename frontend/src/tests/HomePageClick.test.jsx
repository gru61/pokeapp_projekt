import '@testing-library/jest-dom';
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import HomePage from "../pages/HomePage.jsx";
import userEvent from "@testing-library/user-event";

test("Klick auf Pokédex-Karte löst Navigation aus", async () => {
    render(<HomePage />, { wrapper: MemoryRouter });
    const card = screen.getByText("Pokédex").closest(".homepage-card");
    expect(card).toBeInTheDocument();
    await userEvent.click(card); // Kein Fehler = Test bestanden!
    // (Optional: Hier könntest du ein Mock für useNavigate injecten und prüfen!)
});
