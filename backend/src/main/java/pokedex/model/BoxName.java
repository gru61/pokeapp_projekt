package pokedex.model;

import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

/**
 * Enum zur Repräsentation aller im System verfügbaren Boxen (inklusive Team-Box).
 * <p>
 * Jeder Eintrag enthält einen "sprechenden" Anzeigenamen (displayName) zur Verwendung im Frontend.
 * <br>
 * Die Team-Box hat eine Kapazität von max. 6 Pokémon, alle normalen Boxen von max. 20 Pokémon.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Für die Dropdown-Auswahl oder Anzeige von Boxen im UI</li>
 *   <li>Zur Validierung, welche Boxnamen überhaupt gültig sind</li>
 *   <li>Zur Anzeige von freundlichen ("sprechenden") Namen anstelle von Enum-Keys</li>
 * </ul>
 *
 * @author grubi
 */
@Getter
public enum BoxName {
    TEAM("Team"),
    BOX1("Box 1"),
    BOX2("Box 2"),
    BOX3("Box 3"),
    BOX4("Box 4"),
    BOX5("Box 5"),
    BOX6("Box 6"),
    BOX7("Box 7"),
    BOX8("Box 8"),
    BOX9("Box 9"),
    BOX10("Box 10"),
    BOX11("Box 11"),
    BOX12("Box 12");

    /** Der sprechende Name für die Anzeige im Frontend. */
    private final String displayName;

    /**
     * Konstruktor für einen neuen BoxName mit einem Displaynamen.
     * @param displayName Anzeige-Name, der im UI verwendet wird
     */
    BoxName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Liefert eine Liste aller Displaynamen (z.B. für Auswahlfelder im UI).
     *
     * @return Unveränderliche Liste aller Box-Anzeigenamen (inkl. "Team")
     */
    public static List<String> getAllBoxNames() {
        return Stream.of(values())
                .map(BoxName::getDisplayName)
                .toList();
    }
}
