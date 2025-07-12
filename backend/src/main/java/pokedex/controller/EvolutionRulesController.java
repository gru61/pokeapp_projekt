package pokedex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pokedex.service.EvolutionService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EvolutionRulesController {

    private final EvolutionService evolutionService;

    @Autowired
    public EvolutionRulesController(EvolutionService evolutionService) {
        this.evolutionService = evolutionService;
    }

    @GetMapping("/evolution-rules")
    public Map<Integer, List<Integer>> getEvolutionRules() {
        return evolutionService.getEvolutionRules();
    }
}
