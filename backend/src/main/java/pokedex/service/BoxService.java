package pokedex.service;

import pokedex.exception.BoxFullException;
import pokedex.exception.SameBoxException;
import pokedex.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.Edition;
import pokedex.model.OwnedPokemon;
import pokedex.repository.BoxRepository;
import pokedex.repository.OwnedPokemonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service-Klasse zur Verwaltung aller Box-bezogenen Operationen.
 * <p>
 * Stellt Methoden bereit, um Boxen und deren Inhalt zu laden, Kapazitäten zu prüfen
 * und Pokémon zwischen Boxen (und ggf. Editionen) zu verschieben.
 * Validiert dabei die wichtigsten Spielregeln und gibt im Fehlerfall eigene Exceptions aus.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Überprüfen, ob eine Box voll ist</li>
 *   <li>Pokémon von einer Box/Edition in eine andere verschieben (inkl. Validierung der Regeln)</li>
 *   <li>Suche nach einer bestimmten Box anhand Name + Edition</li>
 * </ul>
 *
 * <b>Besonderheiten:</b>
 * <ul>
 *   <li>Transaktional: Die Verschiebung ist atomic, entweder vollständig oder gar nicht.</li>
 *   <li>Wirft spezifische Exceptions bei Regelverletzungen (z.B. Box voll, gleiche Box, nicht gefunden).</li>
 *   <li>Nutzt Logging für Nachvollziehbarkeit wichtiger Aktionen.</li>
 * </ul>
 *
 * @author grubi
 */
@Service
public class BoxService {

    /** Repository für Boxen. */
    private final BoxRepository boxRepo;

    /** Repository für gefangene Pokémon. */
    private final OwnedPokemonRepository ownedRepo;

    /** Logger für Nachvollziehbarkeit und Debugging. */
    private static final Logger logger = LoggerFactory.getLogger(BoxService.class);

    /**
     * Konstruktor für Dependency Injection.
     * @param boxRepo   Repository für Boxen
     * @param ownedRepo Repository für gefangene Pokémon
     */
    public BoxService(BoxRepository boxRepo, OwnedPokemonRepository ownedRepo) {
        this.boxRepo = boxRepo;
        this.ownedRepo = ownedRepo;
    }

    /**
     * Sucht eine Box anhand ihres Namens und der zugehörigen Edition.
     *
     * @param name    Name der Box (TEAM, BOX1, ...)
     * @param edition Edition, zu der die Box gehört
     * @return Die gefundene Box
     * @throws NotFoundException Wenn keine passende Box existiert
     */
    public Box getBoxByNameAndEdition(BoxName name, Edition edition) {
        logger.info("Box mit dem Namen {} aus der Edition {} abgerufen", name, edition);
        return boxRepo.findByNameAndEdition(name, edition)
                .orElseThrow(() -> new NotFoundException("Box nicht gefunden"));
    }

    /**
     * Prüft, ob eine bestimmte Box voll ist (Kapazitätsgrenze erreicht).
     * <ul>
     *   <li>Team-Box: maximal 6 Pokémon</li>
     *   <li>Normale Box: maximal 20 Pokémon</li>
     * </ul>
     *
     * @param name    Name der Box
     * @param edition Edition, zu der die Box gehört
     * @return true, wenn die Box voll ist, sonst false
     */
    public boolean isFull(BoxName name, Edition edition) {
        Box box = getBoxByNameAndEdition(name, edition);
        Long pokemonCount = ownedRepo.countByBox(box);

        if (name == BoxName.TEAM) {
            return pokemonCount >= 6;
        } else {
            return pokemonCount >= 20;
        }
    }

    /**
     * Verschiebt ein Pokémon von einer Quell-Box/-Edition in eine Ziel-Box/-Edition.
     * <p>
     * Prüft dabei u.a.:
     * <ul>
     *   <li>Quell- und Zielbox dürfen nicht identisch sein (sonst {@link SameBoxException})</li>
     *   <li>Pokémon muss in der Quell-Box/-Edition liegen (sonst {@link IllegalStateException})</li>
     *   <li>Ziel-Box darf nicht voll sein (sonst {@link BoxFullException})</li>
     *   <li>Pokémon und Boxen/Editionen müssen existieren ({@link NotFoundException})</li>
     * </ul>
     * Wird als Transaktion ausgeführt.
     *
     * @param pokemonId     Die ID des zu verschiebenden Pokémon
     * @param sourceBox     Name der Quell-Box
     * @param targetBox     Name der Ziel-Box
     * @param sourceEdition Edition der Quell-Box
     * @param targetEdition Edition der Ziel-Box
     * @throws SameBoxException      Wenn Quelle und Ziel identisch sind
     * @throws NotFoundException     Wenn Pokémon oder Zielbox nicht gefunden wurden
     * @throws IllegalStateException Wenn das Pokémon nicht in der Quellbox/-edition liegt
     * @throws BoxFullException      Wenn die Zielbox voll ist
     */
    @Transactional
    public void movePokemon(Long pokemonId, BoxName sourceBox, Edition sourceEdition, BoxName targetBox, Edition targetEdition) {

        // Validierung: Quell- und Zielbox dürfen nicht gleich sein
        if (sourceBox.equals(targetBox) && sourceEdition.equals(targetEdition)) {
            throw new SameBoxException("Du versuchst ein Pokemon in dieselbe Box zu verschieben");
        }

        // Pokémon laden
        OwnedPokemon pokemon = ownedRepo.findById(pokemonId)
                .orElseThrow(() -> new NotFoundException("Pokemon mit der ID " + pokemonId + " nicht gefunden"));

        // Validierung: Pokémon muss sich in der Quell-Box/-Edition befinden
        if (!pokemon.getBox().getName().equals(sourceBox) || !pokemon.getEdition().equals(sourceEdition)) {
            throw new IllegalStateException("Pokemon befindet sich nicht in der angegebenen Quell-Box");
        }

        // Ziel-Box laden
        Box target = getBoxByNameAndEdition(targetBox, targetEdition);

        // Validierung: Ziel-Box darf nicht voll sein
        if (isFull(targetBox, targetEdition)) {
            throw new BoxFullException("Die Ziel Box " + targetBox + " ist schon voll");
        }

        // Verschiebung durchführen
        pokemon.setBox(target);
        pokemon.setEdition(targetEdition);
        logger.info("Pokemon {} erfolgreich von {} aus der Edition {} nach {} Edition {} verschoben",
                pokemonId, sourceBox, sourceEdition, targetBox, targetEdition);
        ownedRepo.save(pokemon);
    }
}
