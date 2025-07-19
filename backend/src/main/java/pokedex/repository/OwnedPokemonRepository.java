package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pokedex.model.Box;
import pokedex.model.OwnedPokemon;

/**
 * Repository-Interface für den Zugriff auf {@link OwnedPokemon}-Entitäten.
 * <p>
 * Erweitert das Spring Data {@link JpaRepository} und bietet Standard-CRUD-Operationen
 * sowie eine spezielle Query zum Zählen der Pokémon in einer bestimmten Box.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Speichern, Laden, Löschen eigener gefangener Pokémon</li>
 *   <li>Prüfung der Belegung einer Box mittels {@link #countByBox(Box)}</li>
 * </ul>
 *
 * <b>Besonderheiten:</b>
 * <ul>
 *   <li>Die Methode {@code countByBox} nutzt eine JPQL-Query, um effizient die Anzahl der Pokémon in einer Box zu ermitteln.</li>
 * </ul>
 *
 * @author grubi
 */
@Repository
public interface OwnedPokemonRepository extends JpaRepository<OwnedPokemon, Long> {

    /**
     * Zählt die Anzahl der Pokémon, die aktuell in der angegebenen Box gespeichert sind.
     *
     * @param box Die Box, für die die Anzahl ermittelt werden soll
     * @return Anzahl der Pokémon in der Box (Long)
     */
    @Query("select count(p) from OwnedPokemon p where p.box = :box")
    Long countByBox(@Param("box") Box box);
}
