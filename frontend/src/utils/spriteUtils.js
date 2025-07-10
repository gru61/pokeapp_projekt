export const getSpriteUrl = (pokedexId) => {
    return pokedexId ? `/assets/sprites/pokemon/${pokedexId}.png` : "/assets/sprites/placeholder.png";
};