package pokedex.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import pokedex.model.BoxName;
import pokedex.model.Edition;

/**
 * Daten-Transfer-Objekt (DTO) für das Erstellen eines neuen eigenen (gefangenen) Pokémon.
 * <p>
 * Wird beim Anlegen eines neuen Pokémon in der API (POST) als Eingabe erwartet.
 * Enthält Validierungsregeln, damit das Frontend gezielt Fehlermeldungen bekommt.
 * </p>
 *
 * <b>Hinweise zur Validierung:</b>
 * <ul>
 *   <li><b>pokedexId</b> darf nicht null sein (Pflichtfeld)</li>
 *   <li><b>level</b> muss zwischen 1 und 100 liegen</li>
 *   <li><b>edition</b> ist Pflicht</li>
 *   <li><b>box</b> ist Pflicht</li>
 *   <li><b>nickname</b> ist optional, maximal 10 Zeichen</li>
 * </ul>
 *
 * <b>Typische Nutzung:</b>
 * <ul>
 *   <li>Wird im Frontend beim Fang-Dialog als "POST /api/pokemon" an das Backend geschickt</li>
 * </ul>
 *
 * @author grubi
 */
@Data
public class CreateOwnedDTO {

    /** Pokédex-Id der Art (Pflichtfeld, z.B. 25 für Pikachu). */
    @NotNull(message = "Die Pokedex ID darf nicht NULL sein")
    private int pokedexId;

    /** Level des gefangenen Pokémon (1 - 100). */
    @Min(value = 1, message = "Why so weak!? (min lvl = 1)")
    @Max(value = 100, message = "Steroide sind nicht gut fürs Pokemon (max lvl = 100)")
    private int level;

    /** Edition, in der das Pokémon gefangen wurde (Pflichtfeld). */
    @NotNull(message = "Wähle eine Edition")
    private Edition edition;

    /** Box, in der das Pokémon gespeichert werden soll (Pflichtfeld). */
    @NotNull(message = "Eine Box muss ausgewählt werden")
    private BoxName box;

    /** Optionaler Nickname (maximal 10 Zeichen). */
    @Size(max = 10, message = "Nickname soll ja nicht länger als der Wahre Name sein")
    private String nickname;
}
