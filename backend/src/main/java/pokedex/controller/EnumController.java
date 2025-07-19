package pokedex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokedex.model.BoxName;
import pokedex.model.Edition;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST-Controller zum Bereitstellen der verfügbaren Enum-Werte (BoxName, Edition) für das Frontend.
 *
 * <p>
 * Bietet Endpunkte, um alle unterstützten Boxnamen und Editionsnamen direkt als Enum-Array oder als Mapping auszugeben.
 * Diese Endpunkte erleichtern insbesondere die Synchronisation der zulässigen Auswahloptionen im UI.
 * </p>
 *
 * Beispielhafte Verwendung im Frontend:
 * <ul>
 *     <li>Laden aller Boxnamen für ein Dropdown-Menü</li>
 *     <li>Anzeige von "sprechenden" Namen (DisplayName) statt Enum-Keys</li>
 * </ul>
 *
 * Pfade beginnen mit <code>/api</code>.
 *
 * @author grubi
 */
@RestController
@RequestMapping("/api")
public class EnumController {

    /**
     * Gibt alle Werte des Enums {@link BoxName} als Array zurück.
     *
     * @return Array aller BoxName-Werte (z.B. für Auswahl im UI)
     */
    @Operation(summary = "Listet alle verfügbaren Boxnamen auf",
            description = "Gibt ein Array aller zulässigen Boxnamen (Enum-Werte) zurück, z.B. für die Anzeige in einem Dropdown-Menü."
    )
    @ApiResponse(responseCode = "200", description = "Array aller Boxnamen wurde erfolgreich geladen")
    @GetMapping("/boxnames")
    public BoxName[] getBoxNames() {
        return BoxName.values();
    }

    /**
     * Gibt ein Mapping aus DisplayName und Enum-Name für alle {@link BoxName}-Werte zurück.
     * <p>
     * Key = Anzeigename (DisplayName), Value = Enum-Konstante.
     * <br>Beispiel: <code>{"Team": "TEAM", "Box 1": "BOX1", ...}</code>
     * <br>Erlaubt es dem Frontend, sowohl eine sprechende Anzeige als auch den technischen Key zu verwenden.
     *
     * @return Map (DisplayName → Enum-Name)
     */
    @Operation(summary = "Liefert ein Mapping aus DisplayName zu Enum-Name für Boxen",
            description = "Gibt eine Map zurück, in der der Key der sprechende Name (DisplayName) und der Value die Enum-Konstante für alle Boxnamen ist. Beispiel: {'Team': 'TEAM', ...}."
    )
    @ApiResponse(responseCode = "200", description = "Mapping aller Boxnamen wurde erfolgreich geladen")
    @GetMapping("/boxnames/mapping")
    public Map<String, String> getBoxNameMapping() {
        return Arrays.stream(BoxName.values())
                .collect(Collectors.toMap(
                        BoxName::getDisplayName,
                        Enum::name
                ));
    }

    /**
     * Gibt alle Werte des Enums {@link Edition} als Array zurück.
     *
     * @return Array aller Edition-Werte (z. B. für Auswahl im UI)
     */
    @Operation(summary = "Listet alle verfügbaren Editionen auf",
            description = "Gibt ein Array aller unterstützten Editionen (Enum-Werte) zurück, z.B. für die Anzeige in einem Dropdown-Menü."
    )
    @ApiResponse(responseCode = "200", description = "Array aller Editionen wurde erfolgreich geladen")
    @GetMapping("/editions")
    public Edition[] getEditions() {
        return Edition.values();
    }

    /**
     * Gibt ein Mapping aus DisplayName und Enum-Name für alle {@link Edition}-Werte zurück.
     * <p>
     * Key = Anzeigename (DisplayName), Value = Enum-Konstante.
     * <br>Beispiel: <code>{"Rot": "ROT", "Blau": "BLAU", ...}</code>
     *
     * @return Map (DisplayName → Enum-Name)
     */
    @Operation(summary = "Liefert ein Mapping aus DisplayName zu Enum-Name für Editionen",
            description = "Gibt eine Map zurück, in der der Key der sprechende Name (DisplayName) und der Value die Enum-Konstante für alle Editionen ist. Beispiel: {'Rot': 'ROT', ...}."
    )
    @ApiResponse(responseCode = "200", description = "Mapping aller Editionen wurde erfolgreich geladen")
    @GetMapping("/editions/mapping")
    public Map<String, String> getEditionMapping() {
        return Arrays.stream(Edition.values())
                .collect(Collectors.toMap(
                        Edition::getDisplayName,
                        Enum::name
                ));
    }
}
