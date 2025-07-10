package pokedex.model;


import lombok.Getter;


/**
 * Enum für die Darstellung der verschiedenen Pokemon Typen im System.
 * Enthält auch lesbare Namen für die Anzeige im Frontend.
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

    private final String displayName;

    PokemonType(String displayName) {
        this.displayName = displayName;
    }
}