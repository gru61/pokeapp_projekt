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
 * Service zur Verwaltung von Boxen und zum Verschieben von Pokemon zwischen den Boxen
 */
@Service
public class BoxService {

    private final BoxRepository boxRepo;
    private final OwnedPokemonRepository ownedRepo;

    private static final Logger logger = LoggerFactory.getLogger(BoxService.class);

    public BoxService(BoxRepository boxRepo, OwnedPokemonRepository ownedRepo) {
        this.boxRepo = boxRepo;
        this.ownedRepo = ownedRepo;
    }

    /**
     * Gibt eine Box anhand dessen Namens zurück.
     * @param name Den Namen der Box (TEAM / BOX1....)
     * @param edition Den Standort der BOX
     * @return Die gefundene Box
     * @throws NotFoundException Wenn die Box nicht gefunden wird
     */
    public Box getBoxByNameAndEdition(BoxName name, Edition edition) {
        logger.info("Box mit dem Namen {} aus der Edition {} abgerufen", name, edition);
        return boxRepo.findByNameAndEdition(name, edition)
                .orElseThrow(() -> new NotFoundException("Box nicht gefunden"));
    }

    /**
     * Prüft, ob die box voll ist.
     * @param name Der Name der Box
     * @param edition Den Standort der BOX
     * @return true, wenn die BOX voll ist
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
     * Verschiebt ein Pokemon von einer Quell-Box in die Ziel-Box
     * @param sourceBox Die aktuelle Box des Pokemon
     * @param targetBox Die Ziel-Box, in die das Pokemon soll
     * @param pokemonId Die ID des zu verschiebenden Pokemon
     * @param sourceEdition Die aktuelle Edition
     * @param targetEdition Die Ziel-Edition
     * @throws SameBoxException Wenn Quell und Ziel Box identisch ist
     * @throws NotFoundException Wenn das gefangene Pokemon nicht gefunden wurde
     * @throws IllegalStateException Wenn dsa Pokemon nicht in der Quell-Box vorhanden ist
     * @throws BoxFullException Wenn die Ziel-Box voll ist
     */
    @Transactional
    public void movePokemon(Long pokemonId, BoxName sourceBox, Edition sourceEdition, BoxName targetBox, Edition targetEdition) {

        //Validierung: Quell und Ziel Box dürfen nicht gleich sein
        if (sourceBox.equals(targetBox) && sourceEdition.equals(targetEdition)) {
            throw new SameBoxException("Du versuchst ein Pokemon in dieselbe Box zu verschieben");
        }

        //Pokemon laden
        OwnedPokemon pokemon = ownedRepo.findById(pokemonId)
                .orElseThrow(() -> new NotFoundException("Pokemon mit der ID " + pokemonId + " nicht gefunden"));

        //Validierung: Pokemon muss sich in der Quell-Box befinden
        if (!pokemon.getBox().getName().equals(sourceBox) || !pokemon.getEdition().equals(sourceEdition)) {
            throw new IllegalStateException("Pokemon befindet sich nicht in der angegebenen Quell-Box");

        }
        //Ziel-Box laden
        Box target = getBoxByNameAndEdition(targetBox, targetEdition);

        //Validierung: Prüft ob der Ziel-Box voll ist
        if (isFull(targetBox, targetEdition)) {
            throw new BoxFullException("Die Ziel Box " + targetBox + " ist schon voll");
        }

        //Speichert die verschiebung
        pokemon.setBox(target);
        pokemon.setEdition(targetEdition);
        logger.info("Pokemon {} erfolgreich von {} aus der Edition {} nach {} Edition {}verschoben",
                pokemonId, sourceBox, sourceEdition, targetBox, targetEdition);
        ownedRepo.save(pokemon);
    }
}
