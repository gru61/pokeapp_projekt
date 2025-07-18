import React from "react";


/**
 * @component PokemonCard
 * @description
 * Zeigt eine Pokémon-Karte mit den wichtigsten Details zu einem einzelnen Pokémon an.
 * Die Karte kann in Grids, Box-Ansichten oder Übersichten verwendet werden. Beim Klick wird
 * das übergebene Callback ausgelöst (z.B. für ein Overlay oder Details).
 *
 * Props:
 * @param {Object} props.mon - Pokémon-Datenobjekt (erwartet alle Infos zu diesem Pokémon)
 * @param {string|number} props.mon.pokedexId - Die Pokédex-ID (wird als 3-stellige Nummer für das Sprite genutzt)
 * @param {string} [props.mon.nickname] - Optionaler Nickname, falls gesetzt
 * @param {string} [props.mon.boxName] - Name der Box (z.B. "Team", "Box 1")
 * @param {string} [props.mon.speciesName] - Artname (z.B. "Bisasam")
 * @param {string} [props.mon.name] - Alternativer Artname, Fallback falls speciesName fehlt
 * @param {number} [props.mon.level] - Das Level des Pokémon
 * @param {string} [props.mon.edition] - Edition/Fassung, zu der das Pokémon gehört
 * @param {string} [props.mon.type1] - Primärer Typ
 * @param {string} [props.mon.type2] - Sekundärer Typ (falls vorhanden und nicht gleich type1)
 * @param {Function} props.onClick - Callback, wird bei Klick auf die Karte ausgelöst
 *
 * Besonderheiten:
 * - Gibt bei fehlendem Sprite ein Platzhalter-Bild aus.
 * - Zeigt, falls vorhanden, Nickname und Artname getrennt an.
 * - Typen werden farbig dargestellt, auch Doppelt-Typen.
 * - Die Edition färbt die Karte (CSS-Klassen wie "edition-gelb", "edition-rot" etc.).
 *
 * @returns {JSX.Element} Die gerenderte Pokémon-Karte
 *
 * @example
 * <PokemonCard mon={mon} onClick={() => setActiveMon(mon)} />
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

