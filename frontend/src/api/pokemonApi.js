const API_BASE = "http://localhost:8000/api";

export const fetchSpecies = async () => {
    const response = await fetch(`${API_BASE}/species`);
    return await response.json();
};

export const fetchOwnedPokemon = async () => {
    const response = await fetch(`${API_BASE}/pokemon`);
    return await response.json();
};

export const addOwnedPokemon = async (data) => {
    const response = await fetch(`${API_BASE}/pokemon`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    });
    return await response.json();
};

export const updateOwnedPokemon = async (id, data) => {
    const response = await fetch(`${API_BASE}/pokemon/${id}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    });
        return await response.json();
};

export const fetchBox = async (edition, name) => {
    const response = await fetch(`${API_BASE}/boxes/${edition}/${name}`);
    return await response.json();
};

export const isBoxFull = async (edition, name) => {
    const response = await fetch(`${API_BASE}/boxes/${edition}/${name}/is-full`);
    return await response.json();
};

export const movePokemon = async (sourceBox, targetBox, pokemonId, sourceEdition, targetEdition) => {
    const response = await fetch(`${API_BASE}/boxes/${sourceBox}/move-to/${targetBox}/${pokemonId}/${sourceEdition}/${targetEdition}`, {
        method: "PUT",
    });
    return response.json();
};