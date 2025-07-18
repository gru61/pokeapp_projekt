package pokedex.dataloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.Edition;

import java.util.Arrays;

/**
 * Initialisiert beim Anwendungsstart für jede Edition die zugehörigen Boxen.
 * <p>
 * Wird automatisch von Spring Boot ausgeführt und stellt sicher,
 * dass für jede Kombination aus Edition und BoxName genau eine Box existiert.
 * Bereits vorhandene Boxen werden nicht erneut angelegt.
 * <br>
 * Diese Daten werden meist einmalig zu Beginn der Anwendung angelegt.
 * </p>
 *
 * <b>Wichtiger Hinweis:</b>
 * Der Loader prüft beim Start, ob bereits Boxen vorhanden sind, und legt sie
 * nur bei Bedarf an. Die Klasse ist als @Component deklariert und wird daher
 * automatisch von Spring erkannt und ausgeführt.
 *
 * @author grubi
 */
@Component
@Transactional
public class BoxDataLoader implements CommandLineRunner {

    /** Logger für Konsolenausgaben und Debugging. */
    private static final Logger logger = LoggerFactory.getLogger(BoxDataLoader.class);

    /** Zugriff auf die JPA-Persistenz. */
    private final EntityManager entityManager;

    /**
     * Konstruktor für Dependency Injection.
     * @param entityManager JPA EntityManager für Datenbankzugriffe
     */
    public BoxDataLoader(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Wird beim Start der Anwendung automatisch von Spring Boot aufgerufen.
     * Prüft, ob bereits Boxen existieren. Falls nein, legt er für jede Edition
     * und jeden Boxnamen genau eine neue Box an.
     *
     * @param args Nicht genutzt
     */
    @Override
    public void run(String... args) {
        // Prüfen, ob bereits Boxen in der Datenbank existieren
        Long count = (Long) entityManager.createQuery("SELECT COUNT(*) FROM Box").getSingleResult();
        if (count != null && count > 0) {
            logger.info("Boxen wurden bereits generiert.");
            return;
        }

        createInitialBoxesForAllEditions();
    }

    /**
     * Erzeugt für jede Edition und jeden Boxnamen eine eigene Box,
     * falls diese Kombination noch nicht existiert.
     */
    private void createInitialBoxesForAllEditions() {
        Arrays.stream(Edition.values()).forEach(edition -> {
            Arrays.stream(BoxName.values()).forEach(boxName -> {
                // Prüfen, ob diese Kombination bereits vorhanden ist
                boolean boxExists = (Long) entityManager.createQuery(
                                "SELECT COUNT(*) FROM Box b WHERE b.name = :name AND b.edition = :edition")
                        .setParameter("name", boxName)
                        .setParameter("edition", edition)
                        .getSingleResult() > 0;

                if (!boxExists) {
                    // Neue Box anlegen, falls nicht vorhanden
                    entityManager.persist(new Box(boxName, edition));
                    logger.info("Box {} für die Edition {} wurde angelegt.", boxName, edition);
                } else {
                    logger.debug("Box {} für die Edition {} existiert bereits.", boxName, edition);
                }
            });
        });

        logger.info("Alle Boxen für alle Editionen erfolgreich geladen.");
    }
}
