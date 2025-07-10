import {useNavigate} from "react-router-dom";
import {getSpriteUrl} from "../utils/spriteUtils";

export default function PokemonCard({species, owned = false}) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/pokedex/${species.pokedexId}`);
    };

    return (
        <div className={"pokemon-card"} onClick={handleClick}>
            <span className={"card-id"}>#{species.pokedexId}</span>
            <img src={getSpriteUrl(species.pokedexId)} alt={species.name}/>
            <h3>{species.name}</h3>
            {owned && <span className={"owned-badge"}>In Besitz</span>}
        </div>
    );
}