package pokedex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pokedex.service.EvolutionService;
import java.util.List;
import java.util.Map;

/**
 * REST-Controller zum Bereitstellen der Entwicklungsregeln aller Pokémon.
 * <p>
 * Über diesen Endpunkt kann das Frontend für jede Pokémon-Art (Pokedex-Id) abfragen,
 * zu welchen Pokedex-Ids das jeweilige Pokémon sich entwickeln kann.
 * <br>Beispielhafte Nutzung: Zur Anzeige oder Validierung von "Entwickeln"-Buttons.
 * </p>
 *
 * <b>Beispiel für Rückgabe:</b>
 * <pre>
 * {
 *   1: [2],        // Bisasam → Bisaknosp
 *   2: [3],        // Bisaknosp → Bisaflor
 *   133: [134,135,136], // Evoli → Aquana, Blitza, Flamara
 *   ...
 * }
 * </pre>
 *
 * @author grubi
 */
@RestController
@RequestMapping("/api")
public class EvolutionRulesController {

    /** Service zum Abrufen der Entwicklungsregeln. */
    private final EvolutionService evolutionService;

    /**
     * Konstruktor für Dependency Injection.
     * @param evolutionService Der Service, der die Entwicklungsregeln bereitstellt
     */
    @Autowired
    public EvolutionRulesController(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    /**
     * Liefert für jede Pokedex-Id eine Liste der möglichen Ziel-Entwicklungen zurück.
     * <p>
     * Die Map enthält als Schlüssel die Pokedex-Id eines Pokémon und als Wert eine Liste von Pokedex-Ids,
     * zu denen sich das jeweilige Pokémon entwickeln kann.
     * <br>Pokémon ohne Entwicklung sind in der Map nicht enthalten oder haben eine leere Liste.
     *
     * @return Map&lt;Pokedex-Id, Liste möglicher Ziel-Pokedex-Ids&gt;
     */
    @GetMapping("/evolution-rules")
    public Map<Integer, List<Integer>> getEvolutionRules() {
        return evolutionService.getEvolutionRules();
    }
}
