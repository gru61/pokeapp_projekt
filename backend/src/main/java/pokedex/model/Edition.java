package pokedex.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Enum zur Repräsentation der verschiedenen Editionen im System (z.B. "Rot", "Blau", "Gelb", "Grün").
 * <p>
 * Jeder Enum-Wert enthält einen "sprechenden" Anzeigenamen (displayName), der für die Nutzeroberfläche bestimmt ist.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Dropdown-Auswahl oder Anzeige der Editionen im UI</li>
 *   <li>Validierung, welche Editionen gültig sind</li>
 *   <li>Anzeige von freundlichen Namen statt Enum-Keys im Frontend</li>
 * </ul>
 *
 * @author grubi
 */
@Getter
public enum Edition {

    GELB("Gelb"),
    ROT("Rot"),
    BLAU("Blau"),
    GRÜN("Grün");

    /** Sprechender Name zur Anzeige im Frontend. */
    private final String displayName;

    /**
     * Konstruktor für einen neuen Editionsnamen mit Anzeigewert.
     * @param displayName Freundlicher Name für die UI
     */
    Edition(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Liefert eine Liste aller Editions-Anzeigenamen (z.B. für Dropdown-Auswahl im UI).
     *
     * @return Unveränderliche Liste aller Editionsnamen
     */
    public static List<String> getAllEditionNames() {
        return Arrays.stream(values())
                .map(Edition::getDisplayName)
                .toList();
    }
}
