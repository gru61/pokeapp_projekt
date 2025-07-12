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
 * Service-Klasse zur Verwaltung von Pokemon-Arten.
 * Bietet Methoden zum Suchen und Abrufen von {@link PokemonSpecies}-Objekten.
 * Diese Klasse ist als Spring-{@link Service} registriert
 */
@Service
public class PokemonSpeciesService {


    private static final Logger logger = LoggerFactory.getLogger(PokemonSpeciesService.class);


    private final PokemonSpeciesRepository speciesRepo;


    public PokemonSpeciesService(PokemonSpeciesRepository speciesRepo) {
        this.speciesRepo = speciesRepo;
    }


    /**
     * Gibt alle Pokemon-Arten zurück
     * @return Liste aller Pokemon-Arten, die Liste kann auch leer sein
     */
    public List<PokemonSpecies> getAllSpecies() {
        return speciesRepo.findAll();
    }


    /**
     * Gibt eine Pokemon-Art anhand dessen Pokedex-ID zurück
     * @param pokedexId die Pokedex-ID der gesuchten Art
     * @return Ein Optional mit der gefundenen Pokemon-Art oder leer, falls keine Art gefunden wurde
     */
    public Optional<PokemonSpecies> getByPokedexId(int pokedexId) {
        logger.info("Pokemon-Art per Pokedex-ID: {} abgerufen", pokedexId);
        return speciesRepo.findByPokedexId(pokedexId);
    }


    /**
     * Gibt eine Pokemon-Art anhand dessen Namens zurück.
     * @param name Der Name der gesuchten Art
     * @return Die gefundenen Pokemon-Art nach Namen
     * @throws NotFoundException Wenn die Art nicht gefunden wurde oder mehrere Ergebnisse vorliegen
     */
    public List<PokemonSpecies> getByName(String name) {
        List<PokemonSpecies> result = speciesRepo.findByName(name);

        if (result.isEmpty()) {
            logger.warn("Keine Pokemon-Art mit diesem Namen {} gefunden", name);
            throw new NotFoundException("Pokemon: " +name + " nicht gefunden" );
        }
        return result;
    }
}
