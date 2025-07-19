package pokedex.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import pokedex.model.BoxName;
import pokedex.model.Edition;

/**
 * Daten-Transfer-Objekt (DTO) für das Aktualisieren eines bereits gefangenen Pokémon.
 * <p>
 * Ermöglicht das gezielte Ändern verschiedener Eigenschaften eines eigenen Pokémon.
 * <br>
 * Typische Anwendungsfälle:
 * <ul>
 *   <li>Entwicklung in eine neue Art (Species-Wechsel)</li>
 *   <li>Umbenennen des Pokémon (Nickname setzen/ändern)</li>
 *   <li>Anpassung des Levels</li>
 *   <li>Wechsel von Box oder Edition</li>
 * </ul>
 * Nur die übergebenen Felder werden tatsächlich geändert ("partial update").
 * </p>
 *
 * <b>Hinweis:</b>
 * Die meisten Felder sind optional, um flexible Updates zu erlauben.
 * Nicht gesetzte Felder werden nicht verändert.
 *
 * <b>Validierungsregeln:</b>
 * <ul>
 *   <li><b>pokedexId</b>: Optional, aber erforderlich für eine Entwicklung (Species-Wechsel). Muss existieren.</li>
 *   <li><b>nickname</b>: Maximal 10 Zeichen, können null sein.</li>
 *   <li><b>level</b>: Zwischen 1 und 100, falls gesetzt.</li>
 *   <li><b>edition</b>: Optional, aber erforderlich bei Wechsel der Edition.</li>
 *   <li><b>box</b>: optional, aber erforderlich bei Wechsel der Box.</li>
 * </ul>
 *
 * <b>Anmerkung:</b>
 * Die auskommentierten {@code @NotNull}-Constraints können bei Bedarf aktiviert werden,
 * falls z. B. im UI verpflichtend bestimmte Felder geändert werden sollen.
 *
 * @author grubi
 */
@Data
@Builder
public class UpdateOwnedDTO {

    /**
     * Die neue Pokédex-ID des Pokémon (nur bei Entwicklung erforderlich).
     * Muss auf einen gültigen und erlaubten Eintrag im Pokédex verweisen.
     * Kann null bleiben, wenn keine Entwicklung erfolgt.
     */
//    @NotNull(message = "Muss eine Pokedex ID haben")
    private Integer pokedexId;

    /**
     * Neuer Nickname für das Pokémon.
     * Optional; falls angegeben, maximal 10 Zeichen.
     */
    @Size(max = 10, message = "Nickname muss zwischen 1 und 10 Zeichen haben")
    private String nickname;

    /**
     * Neues Level des Pokémon (optional).
     * Muss, falls gesetzt, zwischen 1 und 100 liegen und darf nicht kleiner als das aktuelle Level sein.
     */
    @Min(value = 1, message = "Why so weak!? (min lvl = 1)")
    @Max(value = 100, message = "Steroide sind nicht gut fürs Pokemon (max lvl = 100)")
    private Integer level;

    /**
     * Neue Edition des Pokémon (optional).
     * Muss bei einem Wechsel der Edition gesetzt werden.
     */
//    @NotNull(message = "Edition darf nicht leer sein")
    private Edition edition;

    /**
     * Neue Box des Pokémon (optional).
     * Muss bei einem Wechsel der Box gesetzt werden.
     */
//    @NotNull(message = "Box muss angegeben werden")
    private BoxName box;
}
