package pokedex.dataloader;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Initialisiert die Pokémon-Arten (Species) der ersten Generation beim Start der Anwendung.
 * <p>
 * Prüft, ob die Tabelle <code>pokemon_species</code> bereits gefüllt ist.
 * Falls nicht, wird ein SQL-Skript vom Klassenpfad ausgeführt, der alle Einträge anlegt.
 * <br>
 * Die Klasse ist als Spring {@link Component} deklariert und wird bei jedem Anwendungsstart ausgeführt.
 * </p>
 *
 * <b>Hinweis:</b>
 * Der Loader ist idempotent – er prüft vor dem Import, ob bereits Daten vorhanden sind,
 * und verhindert so doppeltes Einfügen der Pokémon-Species.
 * Fehler beim SQL-Import werden im Log ausgegeben und als Exception weiter geworfen.
 *
 * @author grubi
 */
@Component
@Transactional
public class PokemonSpeciesDataLoader implements CommandLineRunner {

    /** Logger für Status- und Fehlermeldungen. */
    private static final Logger logger = LoggerFactory.getLogger(PokemonSpeciesDataLoader.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Konstruktor für Dependency Injection.
     * @param dataSource   Datenquelle für JDBC
     * @param jdbcTemplate JdbcTemplate für einfache DB-Operationen
     */
    public PokemonSpeciesDataLoader(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Wird beim Start der Anwendung automatisch von Spring Boot aufgerufen.
     * <p>
     * Prüft zunächst, ob bereits Einträge in <code>pokemon_species</code> existieren.
     * Falls nein, wird das SQL-Initialisierungsskript <code>first_gen/pokedex/pokemon_species.sql</code>
     * aus dem Klassenpfad ausgeführt, um alle Arten zu importieren.
     *
     * @param args Nicht genutzt.
     * @throws Exception Wird weiter geworfen, falls ein schwerwiegender Fehler beim Laden oder Ausführen des Skripts auftritt.
     */
    @Override
    public void run(String... args) throws Exception {
        // Prüfen, ob die Tabelle schon Daten enthält
        Long count = jdbcTemplate.queryForObject("select count(*) from pokemon_species", Long.class);
        if (count != null && count > 0) {
            logger.info("Pokedex ist bereits vollständig geladen.");
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            // SQL-Skript aus dem Ressourcen-Ordner ausführen
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("first_gen/pokedex/pokemon_species.sql"));
            logger.info("Pokedex erfolgreich aus der SQL-Datei geladen.");
        } catch (SQLException e) {
            logger.error("Datenbankfehler beim Laden des Pokedex: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Fehler beim Laden des Pokedex: {}", e.getMessage(), e);
            throw e;
        }
    }
}
