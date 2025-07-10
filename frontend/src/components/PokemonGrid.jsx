export default function PokemonGrid({pokemons, owned = false}) {
    return (
        <div className={"pokemon-grid"}>
            {pokemons.map(p => (
                <PokemonCard key={p.pokedexId} species={p} owned={owned}/>
            ))}
        </div>
    );
}