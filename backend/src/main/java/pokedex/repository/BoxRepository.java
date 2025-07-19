package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.Edition;

import java.util.Optional;

/**
 * Repository-Interface für Datenbankzugriffe auf {@link Box}-Entitäten.
 * <p>
 * Erweitert das Spring Data {@link JpaRepository} und bietet CRUD-Operationen sowie
 * eine spezielle Abfrage für die Suche nach einer Box anhand ihres Namens und der Edition.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Speichern, Laden und Löschen von Boxen</li>
 *   <li>Spezielle Suche: Box zu gegebener Edition und Name (z.B. "Team", "Box 1" ...)</li>
 * </ul>
 *
 * <b>Hinweis:</b>
 * Die Methode {@link #findByNameAndEdition(BoxName, Edition)} gibt ein Optional zurück,
 * falls keine passende Box gefunden wird.
 *
 * @author grubi
 */
@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {

    /**
     * Sucht eine Box anhand von Name und Edition.
     *
     * @param name    Der Name der Box (z.B. TEAM, BOX1 ...)
     * @param edition Die zugehörige Edition (z.B. ROT, BLAU ...)
     * @return Optional mit der gefundenen Box oder leer, falls nicht vorhanden
     */
    Optional<Box> findByNameAndEdition(BoxName name, Edition edition);
}
