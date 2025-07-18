package pokedex.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import pokedex.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pokedex.exception.InvalidEvolutionException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service zur Verwaltung und Validierung aller Pokémon-Entwicklungen.
 * <p>
 * Dieser Service lädt beim Start eine zentrale JSON-Datei mit den Entwicklungsregeln (Mapping von Pokedex-ID zu erlaubten Ziel-IDs)
 * und stellt Methoden zur Prüfung von erlaubten Entwicklungen bereit.
 * </p>
 *
 * <b>Besonderheiten:</b>
 * <ul>
 *   <li>Lädt und cached die Regeln aus einer externen Datei im Ressourcenpfad ("first_gen/evolutions_rules/evolutions.json")</li>
 *   <li>Wirft bei Fehlern eine {@link InitializationException} bzw. {@link InvalidEvolutionException}</li>
 *   <li>Das Regelwerk kann leicht erweitert werden, ohne Code-Änderung</li>
 * </ul>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Validierung, ob ein Pokémon sich zu einer bestimmten Art entwickeln darf</li>
 *   <li>Anzeige oder Filterung erlaubter Entwicklungen im Frontend</li>
 * </ul>
 *
 * @author grubi
 */
@Service
@Data
public class EvolutionService {

    /** Logger für Fehlermeldungen und Statusausgaben. */
    private static final Logger logger = LoggerFactory.getLogger(EvolutionService.class);

    /**
     * Map mit allen erlaubten Entwicklungen:
     * Key = aktuelle Pokédex-ID, Value = Liste erlaubter Ziel-Pokédex-IDs.
     * <br>Beispiel: 133 → [134, 135, 136] (Evoli kann sich zu drei Formen entwickeln)
     */
    private final Map<Integer, List<Integer>> evolutionRules;

    /**
     * Konstruktor, der beim Start die Entwicklungsregeln aus einer JSON-Ressource lädt und cached.
     * <p>
     * Bei Fehlern (Datei fehlt, Syntaxfehler etc.) wird eine {@link InitializationException} geworfen,
     * damit das System gar nicht erst im fehlerhaften Zustand startet.
     * </p>
     *
     * @param objectMapper Der Jackson-ObjectMapper zum Parsen der JSON-Datei
     * @throws InitializationException Bei Fehlern beim Laden/Parsen der Datei
     */
    public EvolutionService(ObjectMapper objectMapper) {
        try {
            // Versucht, die JSON-Datei aus dem Ressourcenpfad zu laden
            InputStream is = getClass().getClassLoader().getResourceAsStream("first_gen/evolutions_rules/evolutions.json");
            if (is == null) {
                throw new InitializationException("Die Datei wurde nicht gefunden");
            }

            // Parse die JSON-Datei in eine typisierte Map<Integer, List<Integer>>
            this.evolutionRules = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Entwicklungsregeln: {}", e.getMessage(), e);
            throw new InitializationException("Fehler beim initialisieren des EvolutionsService", e);
        }
    }

    /**
     * Prüft, ob eine Entwicklung von einer Art (currentPokedexId) zu einer anderen (targetPokedexId) zulässig ist.
     * <p>
     * Wirft eine {@link InvalidEvolutionException}, wenn diese Entwicklung laut Regeln nicht erlaubt ist.
     * </p>
     *
     * @param currentPokedexId Die aktuelle Pokédex-ID des Pokémon vor der Entwicklung
     * @param targetPokedexId  Die gewünschte Ziel-Pokédex-ID nach der Entwicklung
     * @throws InvalidEvolutionException Wenn die gewünschte Entwicklung laut Regelwerk nicht erlaubt ist
     */
    public void validateEvolution(int currentPokedexId, int targetPokedexId) {
        List<Integer> allowedTargets = evolutionRules.get(currentPokedexId);

        if (allowedTargets == null || !allowedTargets.contains(targetPokedexId)) {
            throw new InvalidEvolutionException("Die Entwicklung von " + currentPokedexId + " zu " + targetPokedexId + " ist nicht erlaubt");
        }
    }
}
