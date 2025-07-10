import "./PokemonCard.css";
import {useNavigate} from "react-router-dom";

function PokemonCard({ name, id }) {
    const navigate = useNavigate();
    const paddedId = id.toString().padStart(3, "0");
    const spriteUrl = `/sprites/${paddedId}.png`;


    const handleClick = () => {
        navigate(`/species/${id}`);
    };

    return (
        <div className="pokemon-card" onClick={handleClick}>
            <img
                src={spriteUrl}
                alt={name}
                className="pokemon-image"
                onError={(e) => {
                    e.target.src = "/sprites/placeholder.png"; // fallback image
                }}
            />
            <h3 className="pokemon-name">{name}</h3>
        </div>
    );
}

export default PokemonCard;