package pokedex.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pokedex.model.BoxName;
import pokedex.model.Edition;


/**
 * DTO für das Erstellen eines neuen gefangenen Pokemon.
 */
@Data
public class CreateOwnedDTO {

    @NotNull(message = "Die Pokedex ID darf nicht NULL sein")
    private int pokedexId;

    @Min(value = 1, message = "Why so weak!? (min lvl = 1)")
    @Max(value = 100, message = "Steroide sind nicht gut fürs Pokemon (max lvl = 100)")
    private int level;

    @NotNull(message = "Wähle eine Edition")
    private Edition edition;

    @NotNull(message = "Eine Box muss ausgewählt werden")
    private BoxName box;

    @Size(max = 10, message = "Nickname soll ja nicht länger als der Wahre Name sein")
    private String nickname;
}