package pokedex.service;

import pokedex.exception.NotFoundException;
import org.springframework.stereotype.Service;
import pokedex.model.PokemonSpecies;
import pokedex.repository.PokemonSpeciesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse zur Verwaltung und Abfrage aller Pokémon-Arten ({@link PokemonSpecies}).
 * <p>
 * Stellt Methoden zum Suchen und Abrufen einzelner oder aller Pokémon-Arten bereit.
 * Ist als Spring-{@link Service} registriert und kapselt die Geschäftslogik rund um Arten/Species.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Laden aller Arten für Pokédex-Ansicht oder Auswahllisten</li>
 *   <li>Suche einer bestimmten Art über Pokédex-ID oder Name</li>
 *   <li>Prüfung auf Existenz oder Eindeutigkeit einer Art</li>
 * </ul>
 *
 * @author grubi
 */
@Service
public class PokemonSpeciesService {

    /** Logger für alle Aktionen rund um Pokémon-Arten. */
    private static final Logger logger = LoggerFactory.getLogger(PokemonSpeciesService.class);

    /** Repository für die persistierten Arten. */
    private final PokemonSpeciesRepository speciesRepo;

    /**
     * Konstruktor für Dependency Injection.
     * @param speciesRepo Repository für alle Pokémon-Arten
     */
    public PokemonSpeciesService(PokemonSpeciesRepository speciesRepo) {
        this.speciesRepo = speciesRepo;
    }

    /**
     * Gibt alle verfügbaren Pokémon-Arten zurück.
     *
     * @return Liste aller gespeicherten Arten ({@link PokemonSpecies}), ggf. leer
     */
    public List<PokemonSpecies> getAllSpecies() {
        return speciesRepo.findAll();
    }

    /**
     * Sucht eine Pokémon-Art anhand der Pokédex-ID.
     *
     * @param pokedexId Pokédex-ID der gesuchten Art
     * @return Optional mit der gefundenen Art, oder leer falls nicht vorhanden
     */
    public Optional<PokemonSpecies> getByPokedexId(int pokedexId) {
        logger.info("Pokemon-Art per Pokedex-ID: {} abgerufen", pokedexId);
        return speciesRepo.findByPokedexId(pokedexId);
    }

    /**
     * Sucht eine oder mehrere Pokémon-Arten anhand eines Namens.
     * <p>
     * Wirft eine {@link NotFoundException}, wenn keine Treffer gefunden werden.
     * </p>
     *
     * @param name Der Name der gesuchten Art
     * @return Liste aller passenden Arten (meistens eine, selten mehrere bei Namensduplikaten)
     * @throws NotFoundException Wenn keine Art mit dem Namen gefunden wurde
     */
    public List<PokemonSpecies> getByName(String name) {
        List<PokemonSpecies> result = speciesRepo.findByName(name);

        if (result.isEmpty()) {
            logger.warn("Keine Pokemon-Art mit diesem Namen {} gefunden", name);
            throw new NotFoundException("Pokemon: " + name + " nicht gefunden");
        }
        return result;
    }
}
