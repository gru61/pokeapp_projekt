package pokedex.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import pokedex.model.BoxName;
import pokedex.model.Edition;
import pokedex.model.OwnedPokemon;


/**
 * DTO dient der Darstellung gefangenen Pokemon's im Frontend.
 * Enthält alle wichtigen Informationen zur Anzeige
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnedPokemonDTO {

    private int id;
    private String nickname;
    private int level;
    private Edition edition;
    private BoxName boxName;
    private int pokedexId;
    private String speciesName;
    private String type1;
    private String type2;

    private OwnedPokemonDTO() {}

    /**
     * Erstellt ein DTO aus einem OwnedPokemon-Objekt
     * @param pokemon Das OwnedPokemon-Objekt
     * @return DTO für die API
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
        dto.setType2(pokemon.getSpecies().getType2() !=null ? pokemon.getSpecies().getType2().getDisplayName() : null);

        return dto;
    }
}
