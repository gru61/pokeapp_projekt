package pokedex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokedex.model.PokemonSpecies;
import pokedex.service.PokemonSpeciesService;

import java.util.List;

/**
 * REST-Controller zur Verwaltung und Abfrage aller bekannten Pokémon-Arten (Species).
 * <p>
 * Stellt Endpunkte bereit, um sämtliche Pokémon-Species (Arten) zu laden
 * oder gezielt nach ID oder Name zu suchen.
 * </p>
 *
 * Typischer Anwendungsfall: Anzeige und Suchfunktion im Pokédex-UI.
 *
 * @author grubi
 */
@RestController
@RequestMapping("/api/species")
public class PokemonSpeciesController {

    /** Service für alle Arten-bezogenen Operationen. */
    private final PokemonSpeciesService speciesService;

    /**
     * Konstruktor für Dependency Injection.
     * @param speciesService Service zum Laden und Suchen von Pokémon-Arten
     */
    public PokemonSpeciesController(PokemonSpeciesService speciesService) {
        this.speciesService = speciesService;
    }

    /**
     * Gibt die vollständige Liste aller Pokémon-Arten (Species) zurück.
     * <p>
     * Wird z. B. zum initialen Laden des Pokédex im Frontend verwendet.
     *
     * @return Liste aller Pokémon-Arten als {@link PokemonSpecies}
     */
    @Operation(summary = "Gibt eine Liste aller 151 Pokémon zurück", description = "Wird für die UI des Pokédex gebraucht")
    @ApiResponse(responseCode = "200", description = "Liste erfolgreich geladen")
    @GetMapping
    public List<PokemonSpecies> getAllSpecies() {
        return speciesService.getAllSpecies();
    }

    /**
     * Sucht gezielt eine Pokémon-Art anhand ihrer Pokédex-ID.
     * <p>
     * Beispiel: <code>/api/species/pokedex-id/25</code> liefert Pikachu.
     *
     * @param pokedexId Die Pokédex-ID der gesuchten Pokémon-Art
     * @return Die gefundene Art als {@link PokemonSpecies}, oder 404 wenn nicht vorhanden
     */
    @Operation(summary = "Sucht eine Pokémon-Art anhand der Pokédex-ID")
    @ApiResponse(responseCode = "200", description = "Pokémon-Art wurde gefunden")
    @ApiResponse(responseCode = "404", description = "Pokémon-Art nicht gefunden")
    @GetMapping("/pokedex-id/{pokedexId}")
    public ResponseEntity<PokemonSpecies> getByPokedexId(@PathVariable int pokedexId) {
        return ResponseEntity.of(speciesService.getByPokedexId(pokedexId));
    }

    /**
     * Sucht eine oder mehrere Pokémon-Arten anhand eines Namensfragments.
     * <p>
     * Gibt eine Liste aller Treffer zurück. Bei keinem Treffer wird HTTP 404 geliefert.
     *
     * @param name Name (oder Fragment) der gesuchten Pokémon-Art (Groß-/Kleinschreibung beachten je nach Implementierung)
     * @return Liste passender Pokémon-Arten; 404, falls keine Treffer
     */
    @Operation(summary = "Sucht Pokémon-Arten nach Name", description = "Gibt eine Liste aller passenden Treffer zurück")
    @ApiResponse(responseCode = "200", description = "Pokémon-Art(en) wurden gefunden")
    @ApiResponse(responseCode = "404", description = "Keine Pokémon-Art gefunden")
    @GetMapping("/name/{name}")
    public ResponseEntity<List<PokemonSpecies>> getByName(@PathVariable String name) {
        List<PokemonSpecies> result = speciesService.getByName(name);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
