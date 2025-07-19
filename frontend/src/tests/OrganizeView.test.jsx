import '@testing-library/jest-dom';
import { render, screen } from "@testing-library/react";
import OrganizeView from "../components/OrganizeView.jsx";

test("zeigt Box-Auswahl für links und rechts an", () => {
    render(<OrganizeView editions={["Rot", "Blau"]} boxes={["Team", "Box 1"]} onBack={() => {}} />);
    expect(screen.getByText("Box links")).toBeInTheDocument();
    expect(screen.getByText("Box rechts")).toBeInTheDocument();
    expect(screen.getAllByRole("combobox").length).toBe(4); // 2x Edition, 2x Box
});
