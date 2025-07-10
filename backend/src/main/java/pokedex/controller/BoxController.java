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


/**
 * Controller zur Verwaltung von Boxen über die REST-API.
 * Enthält Endpunkte zum Laden, Prüfen der Kapazität und Verschieben von Pokemon
 */
@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    private final BoxService boxService;


    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }


    /**
     * Lädt die Box anhand des Namens
     * @param name Der Name der Box
     * @return Die gefundene Box mit allen darin enthaltenen Pokemon
     */
    @Operation(summary = "Lädt eine Box nach ihrem Namen", description = "Gibt als return alle Pokemon welche sich in der Box befindet")
    @ApiResponse(responseCode = "200", description = "Box wurde gefunden")
    @GetMapping(value = "/{edition}/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BoxDTO> getBoxByNameAndEdition(@PathVariable BoxName name, @PathVariable Edition edition) {
        return ResponseEntity.ok(BoxDTO.from(boxService.getBoxByNameAndEdition(name, edition)));
    }


    /**
     * Prüft, ob eine Box voll ist (TEAM=6, BOX=20)
     * @param name Der Name der zu prüfenden Box
     * @return true, wenn die Box voll ist
     */
    @Operation(summary = "Überprüft ob die Ziel Box voll ist", description = "Gibt zurück, ob es noch Platz hat für weitere Pokemon")
    @ApiResponse(responseCode = "200", description = "Ergebnis wurde erfolgreich geladen")
    @ApiResponse(responseCode = "404", description = "Box wurde nicht gefunden")
    @GetMapping("/{edition}/{name}/is-full")
    public ResponseEntity<Boolean> isFull(@PathVariable BoxName name, @PathVariable Edition edition) {
        return ResponseEntity.ok(boxService.isFull(name, edition));
    }


    /**
     * Verschiebt ein Pokemon von einer Box zur anderen.
     * Wird benötigt für die Drag&Drop funktion in der UI
     * @param sourceBox Der aktuelle Box-Name
     * @param targetBox Der Name der Zeil-Box
     * @param pokemonId Die ID des zu verschiebenden Pokemon
     * @param sourceEdition Die aktuelle Edition
     * @param targetEdition Die ziel Edition
     * @return Eine Erfolgsmeldung
     */
    @Operation(summary = "Verschiebt ein Pokemon von Box... zu Box...", description = "Zentraler Endpunkt für drag & drop")
    @ApiResponse(responseCode = "200", description = "Erfolgreich verschoben")
    @ApiResponse(responseCode = "404", description = "Box oder Pokemon nicht gefunden", content = @Content)
    @ApiResponse(responseCode = "409", description = "Ziel Box ist voll | Quellbox gleich Ziel Box", content = @Content)
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
}
