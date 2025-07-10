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
 * Repräsentiert ein gefangenes Pokemon mit den Attributen:
 * - ID, welche von der Datenbank gegeben wird und für klare identifizierung dient
 * - Die Attribute von der Pokemon-Art
 * - Nickname, für eine Optionale vergabe vom User
 * - Level,
 * - Box,
 * - Edition, sind jeweils Mussfelder, um das gefangene Pokemon klar zu dokumentieren und eine klare Übersicht zu bahlten.
 */
@Data
@NoArgsConstructor
@Entity
public class OwnedPokemon {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private PokemonSpecies species;


    private String nickname;


    @Min(value = 1,message = "Why so weak!? (min lvl = 1)")
    @Max(value = 100, message = "Steroide sind nicht gut fürs Pokemon (max lvl = 100)")
    private int level;


    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "box_id",  nullable = false)
    @JsonIgnore
    private Box box;


    @Enumerated(EnumType.STRING)
    @Column(name ="edition", nullable = false)
    private Edition edition;


    public OwnedPokemon(PokemonSpecies species, String nickname, int level, Edition edition, Box box) {
        this.species = species;
        this.nickname = nickname;
        this.level = level;
        this.edition = edition;
        this.box = box;
    }


    /**
     * Überprüft, ob zwei OwnedPokemon-Objekte gleich sind
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
     * Gibt den Hashcode basierend auf de rID zurück
     * @return Der Hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
