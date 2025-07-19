package pokedex.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Entity-Klasse zur Repräsentation einer Box (oder des Teams), in der/dem gefangene Pokémon gespeichert werden.
 * <p>
 * Jede Box ist durch Name und Edition eindeutig (Unique-Constraint).
 * Die Klasse kapselt auch die Logik zur Validierung der Kapazität für das Team (max. 6 Pokémon) bzw. normale Boxen (max. 20).
 * </p>
 *
 * <b>Wesentliche Merkmale:</b>
 * <ul>
 *   <li>Persistente Speicherung in Tabelle "box" mit eindeutiger Kombination aus Name und Edition</li>
 *   <li>Verwaltet eine Liste aller enthaltenen Pokémon (Eintrag in {@link OwnedPokemon})</li>
 *   <li>Kapselt die Kapazitätslogik über die Methode {@link #getCapacity()}</li>
 * </ul>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Speichern und Laden von Boxen und Teams für eine Edition</li>
 *   <li>Prüfen der Kapazität vor Hinzufügen oder Verschieben von Pokémon</li>
 * </ul>
 *
 * @author grubi
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "box", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "edition"})})
public class Box {

    /** Eindeutige ID der Box (Datenbank-Primärschlüssel). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name der Box (z.B. "TEAM", "BOX1", ...), Pflichtfeld. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private BoxName name;

    /** Edition, zu der die Box gehört (z.B. "ROT", "BLAU", ...), Pflichtfeld. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private Edition edition;

    /** Liste aller Pokémon, die aktuell in dieser Box gespeichert sind. */
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private final List<OwnedPokemon> pokemons = new ArrayList<>();

    /**
     * Konstruktor für eine neue Box-Instanz.
     * @param name Name der Box (TEAM oder andere)
     * @param edition Zugehörige Edition
     */
    public Box(BoxName name, Edition edition) {
        this.name = name;
        this.edition = edition;
    }

    /**
     * Gibt die Liste aller enthaltenen Pokémon als unveränderliche Liste zurück.
     * <p>
     * So wird sichergestellt, dass von außen keine Modifikationen an der Liste durchgeführt werden können.
     * </p>
     * @return Unveränderliche Liste der enthaltenen Pokémon
     */
    public List<OwnedPokemon> getPokemons() {
        return Collections.unmodifiableList(pokemons);
    }

    /**
     * Berechnet die maximale Kapazität der Box.
     * <ul>
     *   <li>Rückgabewert 6, wenn es sich um das Team handelt ({@link BoxName#TEAM})</li>
     *   <li>Sonst Rückgabewert 20 für normale Boxen</li>
     * </ul>
     * @return Maximale Anzahl an Pokémon, die in diese Box gespeichert werden können
     */
    public int getCapacity() {
        return name == BoxName.TEAM ? 6 : 20;
    }
}
