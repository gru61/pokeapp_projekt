import React from "react";


/**
 * Renders a Pokémon card component displaying details about a Pokémon.
 *
 * @param {Object} props - The properties passed to the component.
 * @param {Object} props.mon - The Pokémon data object.
 * @param {string} props.mon.pokedexId - The Pokédex ID of the Pokémon.
 * @param {string} [props.mon.nickname] - The nickname of the Pokémon, if any.
 * @param {string} [props.mon.boxName] - The name of the storage box where the Pokémon resides.
 * @param {string} [props.mon.speciesName] - The species name of the Pokémon.
 * @param {string} [props.mon.name] - The name of the Pokémon. Used as a fallback if speciesName is unavailable.
 * @param {number} [props.mon.level] - The level of the Pokémon.
 * @param {string} [props.mon.edition] - The edition or generation of the Pokémon.
 * @param {string} [props.mon.type1] - The primary type of the Pokémon.
 * @param {string} [props.mon.type2] - The secondary type of the Pokémon, if any.
 * @param {Function} props.onClick - The callback function triggered when the Pokémon card is clicked.
 * @return {JSX.Element} The rendered Pokémon card component.
 */
export default function PokemonCard({ mon, onClick }) {
    const spriteNr = String(mon.pokedexId).padStart(3, "0");
    const showNickname = mon.nickname && mon.nickname.trim().length > 0;

    const editionClass = mon.edition ? "edition-" + mon.edition.toLowerCase() : "";

    return (
        <div
            className={`pokemon-card ${editionClass}`}
            onClick={onClick}
        >
            {/* Obere linke Ecke: Edition über dem Boxnamen */}
            {(mon.edition || mon.boxName) && (
                <div className="pokemon-meta-topleft">
                    {mon.edition && (
                        <span className="pokemon-edition">{mon.edition}</span>
                    )}
                    {mon.boxName && (
                        <span className="pokemon-boxname">{mon.boxName}</span>
                    )}
                </div>
            )}

            {/* Pokédex-ID oben rechts */}
            <div className="pokemon-speciesid">#{spriteNr}</div>

            {/* Sprite */}
            <img
                src={`/sprites/${spriteNr}.png`}
                alt={mon.speciesName || mon.name}
                className="pokemon-sprite"
                onError={e => e.target.src = "/sprites/placeholder.png"}
            />

            {/* Name & Nickname */}
            <div className="pokemon-name">
                {showNickname ? (
                    <>
                        <span className="pokemon-nickname">{mon.nickname}</span>
                        <span className="pokemon-realname">{mon.speciesName || mon.name}</span>
                    </>
                ) : (
                    <span>{mon.speciesName || mon.name}</span>
                )}
            </div>

            {/* Level direkt unter dem Namen */}
            {mon.level && (
                <div className="pokemon-level-inline">
                    Lvl. {mon.level}
                </div>
            )}

            {/* Typen */}
            <div className="pokemon-types">
                <span className={`poke-type ${mon.type1?.toLowerCase()}`}>{mon.type1}</span>
                {mon.type2 && mon.type2 !== "NORMAL" && mon.type2 !== mon.type1 && (
                    <span className={`poke-type ${mon.type2.toLowerCase()}`}>{mon.type2}</span>
                )}
            </div>
        </div>
    );
}

