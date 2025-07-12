package pokedex.service;


import pokedex.exception.BoxFullException;
import pokedex.exception.InvalidUpdateException;
import pokedex.exception.NotFoundException;
import org.springframework.stereotype.Service;
import pokedex.dto.CreateOwnedDTO;
import pokedex.dto.UpdateOwnedDTO;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.OwnedPokemon;
import pokedex.model.PokemonSpecies;
import pokedex.repository.OwnedPokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Service zur Verwaltung von gefangenen Pokemon.
 * Enthält Logik zum Hinzufügen, Bearbeiten und Validieren
 */
@Service
public class OwnedPokemonService {


    private static final Logger logger = LoggerFactory.getLogger(OwnedPokemonService.class);


    private final OwnedPokemonRepository ownedRepo;
    private final PokemonSpeciesService speciesService;
    private final BoxService boxService;
    private final EvolutionService evolutionService;


    public OwnedPokemonService(OwnedPokemonRepository ownedRepo,
                               PokemonSpeciesService speciesService,
                               BoxService boxService,
                               EvolutionService evolutionService) {

        this.ownedRepo = ownedRepo;
        this.speciesService = speciesService;
        this.boxService = boxService;
        this.evolutionService = evolutionService;
    }


    /**
     * Gibt alle gefangenen Pokemon zurück.
     * @return Liste aller gefangenen Pokemon
     */
    public List<OwnedPokemon> getAllPokemon() {
        return ownedRepo.findAll();
    }


    /**
     * Gibt ein gefangenes Pokemon anhand dessen ID zurück.
     * @param id Die Id des gesuchten Pokemon
     * @return Das gefundene Pokemon
     * @throws NotFoundException Wenn das Pokemon nicht gefunden wird
     */
    public OwnedPokemon getPokemonById(Long id) {
        logger.info("Suche das gefangene Pokemon per dessen ID. {}", id);
        return ownedRepo.findById(id)
                .orElseThrow(()-> new NotFoundException("Pokemon mit der ID " + id + " nicht gefunden"));
    }

    /**
     * Fügt ein neues Pokemon hinzu
     * @param request Die Eingabedaten
     * @return Das gespeicherte Pokemon
     * @throws NotFoundException Wenn kein Pokemon mit der Pokedex-ID gefunden wurde
     * @throws IllegalStateException Wenn die Ziel-Box nicht angegeben wurde
     * @throws BoxFullException Wenn die Ziel-Box schon voll ist
     */
    public OwnedPokemon addPokemon(CreateOwnedDTO request) {
        logger.info("Füge ein neues gefangenes Pokemon hinzu: {}", request);

        // Lade die Species Daten des gefangenen Pokemon
        PokemonSpecies species = speciesService.getByPokedexId(request.getPokedexId())
                .orElseThrow(() -> new NotFoundException("Kein Pokemon mit dieser Pokedex-ID gefunden"));

        // Validierung der Zielbox
        BoxName targetBox = request.getBox();
        if (targetBox == null) {
            logger.warn("Box wurde nicht angegeben");
            throw new IllegalStateException("Box muss angegeben werden");
        }

        //Prüfung, ob Zielbox voll ist.
        if (boxService.isFull(targetBox, request.getEdition())) {
            String message = (targetBox == BoxName.TEAM)
                    ? "Team ist schon voll (max. 6 Pokemon)"
                    : "Zielbox ist schon voll (max. 20 Pokemon)";
            logger.warn(message);
            throw new BoxFullException(message);
        }

        // Erstellt einen neuen Pokemon Eintrag
        Box box = boxService.getBoxByNameAndEdition(targetBox, request.getEdition());
        OwnedPokemon pokemon = new OwnedPokemon(
                species,
                request.getNickname(),
                request.getLevel(),
                request.getEdition(),
                box);

        logger.info("Neues Pokemon erfolgreich hinzugefügt: {}", pokemon);
        return ownedRepo.save(pokemon);
    }

    /**
     * Aktualisiert ein gefangenes Pokemon
     * @param id Die ID des Pokemon's
     * @param request Die neuen Daten
     * @return Das aktualisierte Pokemon
     */
    public OwnedPokemon updatePokemon(Long id, UpdateOwnedDTO request) {
        OwnedPokemon existing = getPokemonById(id);

        boolean updated = false;

        // Level ändern
        if (existing.getLevel() != (request.getLevel())) {
            if (request.getLevel() < existing.getLevel()) {
                logger.warn("Versuch, das Level eines gefangenen Pokemon zu senken: {}", existing);
                throw new InvalidUpdateException("Level kann nicht gesenkt werden");
            }
            existing.setLevel(request.getLevel());
            logger.info("Level von Pokemon {} aktualisiert auf {}", id, request.getLevel());
            updated = true;
        }

        // Box oder Edition ändern
        boolean boxChanged = !existing.getBox().getName().equals(request.getBox());
        boolean editionChanged = !existing.getEdition().equals(request.getEdition());

        if (boxChanged || editionChanged) {
            boxService.movePokemon(
                    id,
                    existing.getBox().getName(),
                    existing.getEdition(),
                    request.getBox(),
                    request.getEdition()
            );
            logger.info("Box oder Edition von Pokemon {} geändert", id);
            updated = true;
        }

        // Nickname ändern
        if (request.getNickname() != null && !request.getNickname().equals(existing.getNickname())) {
            existing.setNickname(request.getNickname().trim().isEmpty() ? null : request.getNickname());
            logger.info("Nickname von Pokemon {} aktualisiert auf {}", id, request.getNickname());
            updated = true;
        }

        // Evolution versuchen, falls PokedexId anders
        if (request.getPokedexId() != existing.getSpecies().getPokedexId()) {
            evolutionService.validateEvolution(existing.getSpecies().getPokedexId(), request.getPokedexId());
            existing.setSpecies(
                    speciesService.getByPokedexId(request.getPokedexId())
                            .orElseThrow(() -> new NotFoundException("Neue Spezies nicht gefunden"))
            );
            logger.info("Pokemon {} entwickelt zu Pokedex-ID {}", id, request.getPokedexId());
            updated = true;
        }

        if (updated) {
            return ownedRepo.save(existing);
        } else {
            logger.info("Keine Änderungen für Pokemon {} erforderlich", id);
            return existing; // Es wurde nichts geändert
        }
    }

    /**
     * Löscht ein Pokemon anhand dessen ID
     * @param id Die ID das zu löschenden Pokemon
     */
    public void deletePokemonById(Long id) {
        logger.info("Lösche Pokemon anhand der ID {}", id);

        OwnedPokemon pokemon = getPokemonById(id);
        ownedRepo.delete(pokemon);

        logger.info("Pokemon erfolgreich gelöscht: {}", pokemon);
    }
}
