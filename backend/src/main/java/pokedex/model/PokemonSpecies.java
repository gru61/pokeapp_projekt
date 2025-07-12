package pokedex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokedex.dataloader.PokemonSpeciesDataLoader;


/**
 * Repräsentiert eine Art von Pokemon.
 * Jede Art hat eine eindeutige:
 * - Pokedex-ID
 * - Namen
 * - Mindestens einen Typ wenn nicht sogar zwei
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"pokedexId"})
@Entity
public class PokemonSpecies {

    private static final int MAX_NAME_LENGTH = 11;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "pokedex_id", unique = true, nullable = false)
    @Min(value = 1, message = "Was kommt vor der 1? (tipp:DU!)")
    @Max(value = 151, message = "Echte OG's wissen wie vile ePokemon es in der Gen 1 gibt")
    private int pokedexId;


    @NotBlank(message = "Das ist nicht das Haus von Schwarz und Weiss")
    @Column(unique = true)
    @Size(max=MAX_NAME_LENGTH)//längster Name der ersten Gen = Knuddeluff
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(name = "type1", nullable = false)
    private PokemonType type1;


    @Enumerated(EnumType.STRING)
    @Column(name = "type2")
    private PokemonType type2;


    /**
     * Momentan keine Verwendung da die Arten durch die Klasse {@link PokemonSpeciesDataLoader} per SQL geladen werden.
     * Jedoch könnte der Konstruktor in Verwendung kommen, wenn man Tests macht oder als Admin neue Arten einpflegt.
     * @param pokedexId Die ID im nationalen Pokedex
     * @param name Der Name der Pokemon-Art
     * @param type1 Der primäre Typ, muss Feld
     * @param type2 Der sekundäre Tpy, was optional ist
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
