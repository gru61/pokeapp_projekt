package pokedex.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import pokedex.model.BoxName;
import pokedex.model.Edition;
import pokedex.model.OwnedPokemon;

/**
 * Daten-Transfer-Objekt (DTO) zur Übertragung und Anzeige eines eigenen (gefangenen) Pokémon im Frontend.
 * <p>
 * Dieses DTO wird verwendet, um alle relevanten Eigenschaften eines Pokémon vom Backend
 * an das Frontend zu übertragen – z. B. für die Anzeige in Listen, Overlays oder Detailseiten.
 * </p>
 *
 * <b>Hinweise zur Serialisierung:</b>
 * <ul>
 *     <li>Mit <code>@JsonInclude(JsonInclude.Include.NON_NULL)</code> werden Felder mit null-Wert nicht an das Frontend übertragen.</li>
 *     <li>Lombok <code>@Data</code> generiert Getter/Setter und toString/equals automatisch.</li>
 * </ul>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *     <li>Als Antwort-Objekt bei Abfragen aller gefangenen Pokémon ("/api/pokemon")</li>
 *     <li>Zur Anzeige der wichtigsten Eigenschaften in UI-Komponenten (z. B. Karten, Listen, Overlay-Details)</li>
 * </ul>
 *
 * @author grubi
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnedPokemonDTO {

    /** Eindeutige ID des gefangenen Pokémon. */
    private int id;

    /** Optionaler Nickname des Pokémon. */
    private String nickname;

    /** Level des Pokémon. */
    private int level;

    /** Die Edition, aus der Pokémon stammt. */
    private Edition edition;

    /** Box, in der sich das Pokémon aktuell befindet. */
    private BoxName boxName;

    /** Pokédex-ID (Species) dieses Pokémon (z.B. 25 für Pikachu). */
    private int pokedexId;

    /** Name der Pokémon-Art (z. B. "Pikachu"). */
    private String speciesName;

    /** Primärer Typ (z.B. "Elektro"). */
    private String type1;

    /** Sekundärer Typ (kann null sein, z. B. bei Mono-Typen). */
    private String type2;

    /** Privater Standard-Konstruktor, damit nur statische Factory verwendet wird. */
    private OwnedPokemonDTO() {}

    /**
     * Erzeugt ein {@link OwnedPokemonDTO} aus einem {@link OwnedPokemon}-Objekt.
     * <p>
     * Übernimmt alle relevanten Eigenschaften (inklusive verschachtelter Objekte wie Species und Typen).
     * </p>
     *
     * @param pokemon Das Quellobjekt aus dem Model
     * @return Das daraus erzeugte DTO für die API
     */
    public static OwnedPokemonDTO from(OwnedPokemon pokemon) {
        OwnedPokemonDTO dto = new OwnedPokemonDTO();
        dto.setId(pokemon.getId().intValue());
        dto.setNickname(pokemon.getNickname());
        dto.setLevel(pokemon.getLevel());
        dto.setEdition(pokemon.getEdition());
        dto.setBoxName(pokemon.getBox().getName());
        dto.setPokedexId(pokemon.getSpecies().getPokedexId());
        dto.setSpeciesName(pokemon.getSpecies().getName());
        dto.setType1(pokemon.getSpecies().getType1().getDisplayName());
        dto.setType2(
                pokemon.getSpecies().getType2() != null
                        ? pokemon.getSpecies().getType2().getDisplayName()
                        : null
        );
        return dto;
    }
}
