package pokedex.dto;

import lombok.Getter;
import lombok.Setter;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.OwnedPokemon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Daten-Transfer-Objekt (DTO) zur Repräsentation einer Box mit allen enthaltenen Pokémon für die API.
 * <p>
 * Wird verwendet, um Box-Daten inkl. aller enthaltenen Pokémon an das UI/Frontend zu übergeben.
 * Kapselt nur die wichtigsten Eigenschaften (Name, Kapazität, Pokémon-Liste).
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *     <li>Antwortobjekt von REST-Endpunkten, die Informationen über eine oder mehrere Boxen liefern</li>
 *     <li>Abstraktion/Entkopplung zwischen Datenbankmodell und API/Frontend</li>
 * </ul>
 *
 * @author grubi
 */
@Getter
@Setter
public class BoxDTO {

    /** Der Name der Box (z.B. "Team", "Box 1" etc.). */
    private BoxName name;

    /** Maximale Kapazität dieser Box. */
    private int capacity;

    /** Liste aller in der Box enthaltenen eigenen Pokémon als DTO. */
    private List<OwnedPokemonDTO> pokemons;

    /**
     * Erstellt ein neues {@link BoxDTO} aus einer {@link Box}-Entität.
     * <p>
     * Alle enthaltenen {@link OwnedPokemon} werden zu {@link OwnedPokemonDTO} gemappt.
     * Die wichtigsten Eigenschaften (Name, Kapazität, Pokémon-Liste) werden übernommen.
     * </p>
     *
     * @param box Die zu konvertierende Box-Entität
     * @return Eine für das Frontend geeignete DTO-Repräsentation der Box
     */
    public static BoxDTO from(Box box) {
        BoxDTO dto = new BoxDTO();
        dto.setName(box.getName());
        dto.setCapacity(box.getCapacity());
        dto.setPokemons(
                box.getPokemons()
                        .stream()
                        .map(OwnedPokemonDTO::from)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
