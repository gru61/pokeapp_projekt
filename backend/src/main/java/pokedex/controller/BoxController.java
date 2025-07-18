package pokedex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokedex.dto.BoxDTO;
import pokedex.model.BoxName;
import pokedex.model.Edition;
import pokedex.service.BoxService;

import java.util.List;

/**
 * REST-Controller zur Verwaltung von Boxen in der Anwendung.
 * <p>
 * Stellt Endpunkte zum Abrufen, Prüfen der Kapazität und Verschieben von Pokémon zwischen Boxen bereit.
 * Alle Methoden geben eine {@link ResponseEntity} zurück und nutzen zur Serialisierung das DTO {@link BoxDTO}.
 *
 * <p><b>Wichtige Endpunkte:</b></p>
 * <ul>
 *     <li>Laden einer Box inklusive aller enthaltenen Pokémon</li>
 *     <li>Prüfung, ob eine Box voll ist (z.B. für Drag & Drop im UI)</li>
 *     <li>Verschieben eines Pokémon von einer Box/Edition in eine andere</li>
 *     <li>Listen aller gültigen Box- bzw. Editionsnamen</li>
 * </ul>
 *
 * <b>Hinweis:</b> Pfade beinhalten immer die Edition (z. B. /api/boxes/{edition}/{name})
 *
 * @author grubi
 */
@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    /** Service zum Verwalten und Bearbeiten der Boxen. */
    private final BoxService boxService;

    /**
     * Konstruktor für Dependency Injection des BoxService.
     * @param boxService Der zu verwendende BoxService
     */
    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }

    /**
     * Lädt eine Box anhand ihres Namens und der Edition.
     *
     * @param name    Der Name der Box (z. B. "Team", "Box 1" etc.)
     * @param edition Die Edition, zu der die Box gehört
     * @return Die gefundene Box als DTO mit allen enthaltenen Pokémon; 404, falls nicht gefunden
     */
    @Operation(summary = "Lädt eine Box nach ihrem Namen und der Edition",
            description = "Gibt alle Pokémon zurück, die sich in der angegebenen Box befinden.")
    @ApiResponse(responseCode = "200", description = "Box wurde gefunden")
    @GetMapping(value = "/{edition}/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BoxDTO> getBoxByNameAndEdition(@PathVariable BoxName name, @PathVariable Edition edition) {
        return ResponseEntity.ok(BoxDTO.from(boxService.getBoxByNameAndEdition(name, edition)));
    }

    /**
     * Prüft, ob die angegebene Box voll ist.
     * <p>
     * Es gelten die Regeln: Team-Box = max. 6, normale Box = max. 20 Pokémon.
     *
     * @param name    Name der zu prüfenden Box
     * @param edition Edition der Box
     * @return true, wenn die Box voll ist; false, wenn noch Platz frei ist
     */
    @Operation(summary = "Prüft, ob die Zielbox voll ist",
            description = "Gibt zurück, ob noch Platz für weitere Pokémon vorhanden ist.")
    @ApiResponse(responseCode = "200", description = "Ergebnis wurde erfolgreich geladen")
    @ApiResponse(responseCode = "404", description = "Box wurde nicht gefunden")
    @GetMapping("/{edition}/{name}/is-full")
    public ResponseEntity<Boolean> isFull(@PathVariable BoxName name, @PathVariable Edition edition) {
        return ResponseEntity.ok(boxService.isFull(name, edition));
    }

    /**
     * Verschiebt ein Pokémon von einer Quellbox in eine Zielbox, ggf. auch über Editionsgrenzen hinweg.
     * <p>
     * Wird insbesondere von der Drag & Drop-Funktion im Frontend genutzt.
     *
     * @param pokemonId     Die ID des zu verschiebenden Pokémon
     * @param sourceBox     Name der Quellbox
     * @param sourceEdition Edition der Quellbox
     * @param targetBox     Name der Zielbox
     * @param targetEdition Edition der Zielbox
     * @return HTTP 204 (No Content) bei Erfolg, HTTP 404 oder 409 bei Fehlern (z. B. Zielbox voll)
     */
    @Operation(summary = "Verschiebt ein Pokémon von einer Box zu einer anderen (auch Editions übergreifend)",
            description = "Wird für Drag & Drop genutzt. Prüft auf Kollisionen, z.B. ob die Zielbox voll ist.")
    @ApiResponse(responseCode = "200", description = "Pokémon wurde erfolgreich verschoben")
    @ApiResponse(responseCode = "404", description = "Box oder Pokémon nicht gefunden", content = @Content)
    @ApiResponse(responseCode = "409", description = "Zielbox ist voll oder Quelle/Ziel identisch", content = @Content)
    @PutMapping("/{sourceBox}/move-to/{targetBox}/{pokemonId}/{sourceEdition}/{targetEdition}")
    public ResponseEntity<Void> movePokemon (
            @PathVariable Long pokemonId,
            @PathVariable BoxName sourceBox,
            @PathVariable Edition sourceEdition,
            @PathVariable BoxName targetBox,
            @PathVariable Edition targetEdition) {

        boxService.movePokemon(pokemonId, sourceBox, sourceEdition, targetBox, targetEdition);
        return ResponseEntity.noContent().build();
    }

    /**
     * Liefert alle gültigen Box-Namen zurück.
     *
     * @return Liste aller Box-Namen (z. B. "TEAM", "BOX1", BOX2)
     */
    @Operation(summary = "Gibt alle verfügbaren Box-Namen zurück",
            description = "Nützlich für Dropdowns im Frontend.")
    @ApiResponse(responseCode = "200", description = "Box-Namen erfolgreich geladen")
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllBoxNames() {
        return ResponseEntity.ok(BoxName.getAllBoxNames());
    }

    /**
     * Liefert alle unterstützten Editionsnamen zurück.
     *
     * @return Liste aller verfügbaren Editionsnamen (z. B. "ROT", "BLAU", "GELB", etc)
     */
    @Operation(summary = "Gibt alle verfügbaren Editionsnamen zurück",
            description = "Nützlich für Auswahlfelder im Frontend.")
    @ApiResponse(responseCode = "200", description = "Editionsnamen erfolgreich geladen")
    @GetMapping("/editions")
    public ResponseEntity<List<String>> getAllEditionNames() {
        return ResponseEntity.ok(Edition.getAllEditionNames());
    }

}
