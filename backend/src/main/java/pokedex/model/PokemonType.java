package pokedex.model;

import lombok.Getter;

/**
 * Enum zur Repräsentation aller Pokémon-Typen der ersten Generation.
 * <p>
 * Jeder Typ besitzt einen sprechenden Anzeigenamen (<code>displayName</code>), der für das Frontend (UI)
 * und zur Anzeige in API/DTOs verwendet wird. Dies ermöglicht es, technische Enum-Namen von den Nutzertexten zu entkoppeln.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Zuordnung und Anzeige der Typen bei {@link PokemonSpecies}</li>
 *   <li>Darstellung von Typen in Oberflächen, z.B. farbige Badges oder Filter</li>
 *   <li>Validierung erlaubter Typen in Services und DTOs</li>
 * </ul>
 *
 * <b>Enthaltene Typen (Gen 1):</b>
 * Normal, Pflanze, Gift, Feuer, Flug, Wasser, Käfer, Elektro, Boden, Kampf, Psycho, Gestein, Eis, Geist, Drache
 *
 * @author grubi
 */
@Getter
public enum PokemonType {
    NORMAL("Normal"),
    PFLANZE("Pflanze"),
    GIFT("Gift"),
    FEUER("Feuer"),
    FLUG("Flug"),
    WASSER("Wasser"),
    KÄFER("Käfer"),
    ELEKTRO("Elektro"),
    BODEN("Boden"),
    KAMPF("Kampf"),
    PSYCHO("Psycho"),
    GESTEIN("Gestein"),
    EIS("Eis"),
    GEIST("Geist"),
    DRACHE("Drache");

    /** Anzeigename des Typs für das Frontend/DTOs. */
    private final String displayName;

    /**
     * Konstruktor für einen neuen Pokémon-Typ mit Anzeigenamen.
     * @param displayName Nutzerfreundlicher Name für UI/API
     */
    PokemonType(String displayName) {
        this.displayName = displayName;
    }
}
