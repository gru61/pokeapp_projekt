package pokedex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
* Diese Klasse soll als die Boxen und das Team repräsentieren, in denen die gefangenen Pokemon gespeichert werden.
* Enthält zusätzlich die Logik zur Validierung der Kapazitäten der einzelnen Boxen und des Teams
*/

@Entity
@Getter
@NoArgsConstructor
@Table(name = "box", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "edition"})})
public class Box {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private BoxName name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Edition edition;


    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private final List<OwnedPokemon> pokemons = new ArrayList<>();


    public Box(BoxName name, Edition edition) {
        this.name = name;
        this.edition = edition;
    }


    public List<OwnedPokemon> getPokemons() {
        return Collections.unmodifiableList(pokemons);
    }


    /**
     * Gibt die maximale Kapazität der ausgewählten Box zurück
     * @return 6 für TEAM sonst 20 für Box
     */
    public int getCapacity() {
        return name == BoxName.TEAM ? 6 : 20;

    }
}
