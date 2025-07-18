package pokedex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokedex.dataloader.PokemonSpeciesDataLoader;

/**
 * Entity-Klasse zur Repräsentation einer Pokémon-Art (Species) der ersten Generation.
 * <p>
 * Jede Art hat:
 * <ul>
 *   <li><b>Pokedex-ID:</b> Eindeutige Nummer aus dem nationalen Pokédex (1-151 für Gen 1)</li>
 *   <li><b>Name:</b> Name der Art, maximal 11 Zeichen</li>
 *   <li><b>Primär-Typ:</b> Pflichtfeld ({@link PokemonType})</li>
 *   <li><b>Sekundär-Typ:</b> Optional ({@link PokemonType}), kann null sein</li>
 * </ul>
 * Die Instanzen werden überwiegend automatisch per SQL-Skript vom {@link PokemonSpeciesDataLoader} importiert,
 * können aber auch manuell (z.B. in Tests oder als Admin) angelegt werden.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Verknüpfung mit eigenen gefangenen Pokémon ({@link pokedex.model.OwnedPokemon})</li>
 *   <li>Anzeige im Pokédex und beim Hinzufügen eines neuen Pokémon</li>
 *   <li>Validierung, ob eine ID/Namen-Kombination zulässig ist</li>
 * </ul>
 *
 * <b>Validierungsregeln:</b>
 * <ul>
 *   <li>Pokedex-ID: 1 bis 151 (Gen 1), eindeutig, Pflicht</li>
 *   <li>Name: Nicht leer, eindeutig, max. 11 Zeichen</li>
 *   <li>type1: Pflichtfeld</li>
 *   <li>type2: Optional</li>
 * </ul>
 *
 * @author grubi
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"pokedexId"})
@Entity
public class PokemonSpecies {

    /** Maximale Länge für einen Pokémon-Namen in Gen 1 (z.B. "Knuddeluff"). */
    private static final int MAX_NAME_LENGTH = 11;

    /** Datenbank-Primärschlüssel (wird intern vergeben). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nationale Pokédex-ID (1-151), eindeutig, Pflichtfeld. */
    @Column(name = "pokedex_id", unique = true, nullable = false)
    @Min(value = 1, message = "Was kommt vor der 1? (tipp:DU!)")
    @Max(value = 151, message = "Echte OG's wissen wie vile ePokemon es in der Gen 1 gibt")
    private int pokedexId;

    /** Name der Pokémon-Art, eindeutig, nicht leer, max. 11 Zeichen. */
    @NotBlank(message = "Das ist nicht das Haus von Schwarz und Weiss")
    @Column(unique = true)
    @Size(max=MAX_NAME_LENGTH) // längster Name der ersten Gen = Knuddeluff
    private String name;

    /** Primärer Typ (z.B. "Wasser", "Feuer"), Pflichtfeld. */
    @Enumerated(EnumType.STRING)
    @Column(name = "type1", nullable = false)
    private PokemonType type1;

    /** Sekundärer Typ (optional, z.B. "Flug", "Gift", ...). */
    @Enumerated(EnumType.STRING)
    @Column(name = "type2")
    private PokemonType type2;

    /**
     * Konstruktor für Tests und manuelle Anlage (z.B. als Admin).
     * Für den normalen Betrieb werden Instanzen per SQL vom {@link PokemonSpeciesDataLoader} importiert.
     *
     * @param pokedexId Pokédex-ID (Pflicht)
     * @param name      Name der Art (Pflicht, max. 11 Zeichen)
     * @param type1     Primärer Typ (Pflicht)
     * @param type2     Sekundärer Typ (optional)
     */
    public PokemonSpecies(int pokedexId, String name, PokemonType type1, PokemonType type2) {
        if (type1 == null) {
            throw new IllegalArgumentException("Type 1 kann nicht null sein");
        }
        this.pokedexId = pokedexId;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
    }

    /**
     * Gibt eine kurze String-Repräsentation dieser Pokémon-Art aus.
     * @return Menschlich lesbarer String mit den wichtigsten Attributen
     */
    @Override
    public String toString() {
        return "PokemonSpecies{" +
                "id=" + id +
                ", PokedexId=" + pokedexId +
                ", Name='" + name + '\'' +
                ", Type1='" + type1 + '\'' +
                ", Type2='" + type2 + '\'' +
                '}';
    }
}
