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
 * Lädt die Pokemon-Daten (erste Generation) beim Start der Anwendung,
 * fals die tabelle 'pokemon_species' noch leer ist.
 */
@Component
@Transactional
public class PokemonSpeciesDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PokemonSpeciesDataLoader.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public PokemonSpeciesDataLoader(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        Long count = jdbcTemplate.queryForObject("select count(*) from pokemon_species", Long.class);
        if (count != null && count >0) {
            logger.info("Pokedex ist bereits vollständig geladen.");
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
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