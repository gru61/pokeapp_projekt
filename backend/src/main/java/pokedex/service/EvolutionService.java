package pokedex.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import pokedex.exception.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pokedex.exception.InvalidEvolutionException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service zur Prüfung der Entwicklungen der Pokemon
 */
@Service
public class EvolutionService {


    private static final Logger logger = LoggerFactory.getLogger(EvolutionService.class);

    private final Map<Integer, List<Integer>> evolutionRules;


    /**
     * Lädt die Datei für die Entwicklungsregeln und initialisiert diese
     * @param objectMapper Der ObjectMapper zum Parsen (umwandlung als JAVA-Objekt) der JSON-Datei
     * @throws InitializationException Gibt an, ob ein Fehler beim Initialisieren der Datei aufgetreten ist
     */
    public EvolutionService(ObjectMapper objectMapper) {
        try {
            //Versucht die JSON Datei aus dem PATH zu laden
            InputStream is = getClass().getClassLoader().getResourceAsStream("first_gen/evolutions_rules/evolutions.json");
            if (is == null) {
                throw new InitializationException("Die Datei wurde nicht gefunden");
            }

            // Parse die JSON Datei in eine typensichere Map
            this.evolutionRules = objectMapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            logger.error("Fehler beim Laden der Entwicklungsregeln: {}", e.getMessage(),e);
            throw new InitializationException("Fehler beim initialisieren des EvolutionsService",e);
        }
    }

    /**
     * Überprüft, ob die Entwicklung des Pokemons so stattfinden kann
     * @param currentPokedexId Die Pokedex-ID vor der Pokemons
     * @param targetPokedexId Die Pokedex-ID nach der Entwicklung
     * @throws InvalidEvolutionException Gibt an, ob ein Fehler bei der Evolution aufgetreten sind
     */
    public void validateEvolution(int currentPokedexId, int targetPokedexId) {
        List<Integer> allowedTargets = evolutionRules.get(currentPokedexId);

        if (allowedTargets == null || !allowedTargets.contains(targetPokedexId)) {
            throw new InvalidEvolutionException("Die Entwicklung von " +currentPokedexId + " zu " +targetPokedexId + " ist nicht erlaubt" );
        }
    }
}

