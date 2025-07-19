package pokedex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Entity-Klasse zur Repräsentation eines gefangenen Pokémon mit allen Attributen,
 * die zur individuellen Verwaltung im System nötig sind.
 * <p>
 * Zu den Attributen zählen:
 * <ul>
 *   <li><b>ID:</b> Eindeutige Datenbank-ID für Identifizierung und Vergleich</li>
 *   <li><b>species:</b> Verweis auf die Pokémon-Art ({@link PokemonSpecies}), Pflichtfeld</li>
 *   <li><b>nickname:</b> Optionaler, vom Nutzer gewählter Spitzname</li>
 *   <li><b>level:</b> Aktuelles Level des Pokémon (zwischen 1 und 100, Pflichtfeld)</li>
 *   <li><b>box:</b> Zugehörige Box, in der das Pokémon gespeichert ist, Pflichtfeld</li>
 *   <li><b>edition:</b> Zugehörige Edition (z.B. Rot/Blau/Gelb/Grün), Pflichtfeld</li>
 * </ul>
 * Die Klasse bietet Standardimplementierungen für equals/hashCode (nur auf Basis der ID!).
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Speicherung, Anzeige und Verwaltung eigener gefangener Pokémon</li>
 *   <li>Verwendung als Referenz bei Boxen und Editionen</li>
 *   <li>DTO-Mapping für API-Übertragung</li>
 * </ul>
 *
 * @author grubi
 */
@Data
@NoArgsConstructor
@Entity
public class OwnedPokemon {

    /** Eindeutige Datenbank-ID des gefangenen Pokémon. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Zugehörige Pokémon-Art (z.B. "Pikachu"), Pflichtfeld. */
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private PokemonSpecies species;

    /** Optionaler Nickname für dieses Pokémon. */
    private String nickname;

    /** Level dieses Pokémon (1-100, Pflichtfeld mit Validation). */
    @Min(value = 1, message = "Why so weak!? (min lvl = 1)")
    @Max(value = 100, message = "Steroide sind nicht gut fürs Pokemon (max lvl = 100)")
    private int level;

    /** Zugehörige Box (z.B. "Team", "Box 1", ...), Pflichtfeld. */
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "box_id", nullable = false)
    @JsonIgnore
    private Box box;

    /** Zugehörige Edition, Pflichtfeld. */
    @Enumerated(EnumType.STRING)
    @Column(name = "edition", nullable = false)
    private Edition edition;

    /**
     * Konstruktor für ein neues gefangenes Pokémon mit allen Pflichtfeldern.
     * @param species  Pokémon-Art (z.B. Pikachu)
     * @param nickname Optionaler Spitzname
     * @param level    Pokémon-Level
     * @param edition  Zugehörige Edition
     * @param box      Zugehörige Box
     */
    public OwnedPokemon(PokemonSpecies species, String nickname, int level, Edition edition, Box box) {
        this.species = species;
        this.nickname = nickname;
        this.level = level;
        this.edition = edition;
        this.box = box;
    }

    /**
     * Prüft, ob zwei {@code OwnedPokemon}-Objekte gleich sind.
     * <p>
     * Zwei Pokémon gelten als gleich, wenn ihre Datenbank-IDs übereinstimmen.
     * </p>
     *
     * @param o Das zu vergleichende Objekt
     * @return true, wenn die IDs übereinstimmen
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OwnedPokemon other)) return false;
        return id != null && id.equals(other.id);
    }

    /**
     * Gibt den Hashcode auf Basis der ID zurück.
     * @return Hashcode dieses Pokémon
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
