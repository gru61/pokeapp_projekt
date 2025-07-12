package pokedex.dto;

import lombok.Getter;
import lombok.Setter;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.OwnedPokemon;

import java.util.List;
import java.util.stream.Collectors;


/**
 * DTO zur Darstellung einer Box mit den enthaltenden Pokemon.
 * Wird genutzt, um Bax Daten an das UI zu übergeben
 */
@Getter
@Setter
public class BoxDTO {
    private BoxName name;
    private int capacity;
    private List<OwnedPokemonDTO> pokemons;


    /**
     * Erstellt ein {@link BoxDTO}-Objekt aus einer {@link Box}-Entität.
     * Die Methode übernimmt die wichtigsten Eigenschaften der Box-Name, Kapazität und
     * enthaltende Pokemon - und mapping jedes enthaltene {@link OwnedPokemon} via
     * {@link OwnedPokemonDTO#from(OwnedPokemon)} ein ein DTO für die API-Ausgabe.
     * @param box Die zu konvertierende Box-Entität
     * @return Eine für das Frontend geeignete DTO-Repräsentation der Box
     */
    public static BoxDTO from (Box box) {
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
