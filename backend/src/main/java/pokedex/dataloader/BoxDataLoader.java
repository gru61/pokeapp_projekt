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
 * Lädt alle Boxen für jede Edition beim App-Start.
 * Stellt sicher, dass jede Box-Edition-Kombination eindeutig ist.
 */
@Component
@Transactional
public class BoxDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BoxDataLoader.class);

    private final EntityManager entityManager;

    public BoxDataLoader(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void run(String... args) {
        // Prüfen, ob Boxen bereits angelegt wurden
        Long count = (Long) entityManager.createQuery("SELECT COUNT(*) FROM Box").getSingleResult();
        if (count != null && count > 0) {
            logger.info("Boxen wurden bereits generiert.");
            return;
        }

        createInitialBoxesForAllEditions();
    }

    private void createInitialBoxesForAllEditions() {
        Arrays.stream(Edition.values()).forEach(edition -> {
            Arrays.stream(BoxName.values()).forEach(boxName -> {
                boolean boxExists = (Long) entityManager.createQuery("SELECT COUNT(*) FROM Box b WHERE b.name = :name AND b.edition = :edition")
                        .setParameter("name", boxName)
                        .setParameter("edition", edition)
                        .getSingleResult() > 0;

                if (!boxExists) {
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