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
 * Service zur Verwaltung und Validierung aller gefangenen Pokémon des Nutzers.
 * <p>
 * Stellt die zentrale Logik für das Hinzufügen, Bearbeiten, Löschen und Validieren von gefangenen Pokémon bereit.
 * Prüft auf Einhaltung der Spielregeln (z.B. Box-Kapazität, Level, Evolution) und sorgt für die Interaktion mit
 * Boxen, Arten und Evolutionsregeln.
 * </p>
 *
 * <b>Besonderheiten:</b>
 * <ul>
 *   <li>Validiert alle Business Rules für gefangene Pokémon</li>
 *   <li>Wirft passende Exceptions (z.B. {@link BoxFullException}, {@link InvalidUpdateException}, {@link NotFoundException})</li>
 *   <li>Verwendet Logging für alle wichtigen Aktionen und Fehlerfälle</li>
 *   <li>Nutzen von Service-Klassen für Species, Boxen und Evolutionsregeln</li>
 * </ul>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>UI- und API-Aufrufe zum Listen, Hinzufügen, Bearbeiten und Löschen von Pokémon</li>
 *   <li>Automatische Validierung vor Speichern von Änderungen</li>
 * </ul>
 *
 * @author grubi
 */
@Service
public class OwnedPokemonService {

    /** Logger für alle wichtigen Aktionen. */
    private static final Logger logger = LoggerFactory.getLogger(OwnedPokemonService.class);

    /** Repository für gefangene Pokémon. */
    private final OwnedPokemonRepository ownedRepo;
    /** Service für Pokémon-Arten. */
    private final PokemonSpeciesService speciesService;
    /** Service für Boxen/Teams. */
    private final BoxService boxService;
    /** Service für Evolutionsregeln. */
    private final EvolutionService evolutionService;

    /**
     * Konstruktor für Dependency Injection.
     * @param ownedRepo        Repository für eigene Pokémon
     * @param speciesService   Service für Arten
     * @param boxService       Service für Boxen/Teams
     * @param evolutionService Service für Evolutionsregeln
     */
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
     * Gibt eine Liste aller gefangenen Pokémon zurück.
     *
     * @return Liste aller eigenen Pokémon
     */
    public List<OwnedPokemon> getAllPokemon() {
        return ownedRepo.findAll();
    }

    /**
     * Sucht ein gefangenes Pokémon anhand seiner ID.
     *
     * @param id Die ID des gesuchten Pokémon
     * @return Das gefundene Pokémon
     * @throws NotFoundException Wenn das Pokémon nicht existiert
     */
    public OwnedPokemon getPokemonById(Long id) {
        logger.info("Suche das gefangene Pokemon per dessen ID. {}", id);
        return ownedRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Pokemon mit der ID " + id + " nicht gefunden"));
    }

    /**
     * Fügt ein neues gefangenes Pokémon hinzu.
     * <p>
     * Validiert, dass die Pokémon-Art existiert, die Box angegeben ist und nicht voll ist.
     * </p>
     *
     * @param request Die Eingabedaten für das neue Pokémon
     * @return Das gespeicherte Pokémon
     * @throws NotFoundException    Wenn die Pokémon-Art nicht existiert
     * @throws IllegalStateException Wenn keine Ziel-Box angegeben wurde
     * @throws BoxFullException     Wenn die Ziel-Box bereits voll ist
     */
    public OwnedPokemon addPokemon(CreateOwnedDTO request) {
        logger.info("Füge ein neues gefangenes Pokemon hinzu: {}", request);

        // Lade die Species-Daten des gefangenen Pokémon
        PokemonSpecies species = speciesService.getByPokedexId(request.getPokedexId())
                .orElseThrow(() -> new NotFoundException("Kein Pokemon mit dieser Pokedex-ID gefunden"));

        // Validierung der Ziel-Box
        BoxName targetBox = request.getBox();
        if (targetBox == null) {
            logger.warn("Box wurde nicht angegeben");
            throw new IllegalStateException("Box muss angegeben werden");
        }

        // Prüfung, ob Ziel-Box voll ist.
        if (boxService.isFull(targetBox, request.getEdition())) {
            String message = (targetBox == BoxName.TEAM)
                    ? "Team ist schon voll (max. 6 Pokemon)"
                    : "Zielbox ist schon voll (max. 20 Pokemon)";
            logger.warn(message);
            throw new BoxFullException(message);
        }

        // Erstellt einen neuen Pokémon-Eintrag
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
     * Aktualisiert ein gefangenes Pokémon.
     * <p>
     * Alle Felder (Level, Box, Edition, Nickname, Entwicklung) werden geprüft und ggf. aktualisiert.
     * Spielregeln (z.B. Level nur erhöhen, erlaubte Evolution) werden geprüft.
     * </p>
     *
     * @param id      Die ID des Pokémon
     * @param request Die neuen Daten (nur geänderte Felder werden übernommen)
     * @return Das aktualisierte Pokémon
     * @throws InvalidUpdateException   Wenn Regeln verletzt werden (z.B. Level gesenkt)
     * @throws NotFoundException        Wenn Pokémon oder neue Species nicht existieren
     * @throws BoxFullException        Wenn die neue Ziel-Box voll ist
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
     * Löscht ein gefangenes Pokémon anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Pokémon
     * @throws NotFoundException Wenn das Pokémon nicht gefunden wird
     */
    public void deletePokemonById(Long id) {
        logger.info("Lösche Pokemon anhand der ID {}", id);

        OwnedPokemon pokemon = getPokemonById(id);
        ownedRepo.delete(pokemon);

        logger.info("Pokemon erfolgreich gelöscht: {}", pokemon);
    }
}
