import '@testing-library/jest-dom';
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import HomePage from "../pages/HomePage.jsx";

test("zeigt drei große Navigationskarten an", () => {
    render(<HomePage />, { wrapper: MemoryRouter });
    expect(screen.getByText("Pokédex")).toBeInTheDocument();
    expect(screen.getByText("Eigene Pokémon")).toBeInTheDocument();
    expect(screen.getByText("Boxen")).toBeInTheDocument();
});
