package pokedex.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pokedex.dto.CreateOwnedDTO;
import pokedex.dto.OwnedPokemonDTO;
import pokedex.dto.UpdateOwnedDTO;
import pokedex.exception.NotFoundException;
import pokedex.model.OwnedPokemon;
import pokedex.service.OwnedPokemonService;

import java.util.List;


/**
 * Controller zu Verwaltung gefangener Pokemon über die REST-API.
 * Bietet Endpunkte zum Hinzufügen, Bearbeiten, Löschen und Laden von Pokemon
 */
@RestController
@RequestMapping("/api/pokemon")
public class OwnedPokemonController {

    private final OwnedPokemonService ownedService;


    public OwnedPokemonController(OwnedPokemonService ownedService) {
        this.ownedService = ownedService;
    }


    /**
     * Gibt alle gefangenen Pokemon zurück
     * @return Liste aller gefangenen Pokemon
     */
    @Operation(summary = "Lädt alle gefangenen Pokemon", description = "Gibt eine Liste aller gespeicherten Pokemon zurück")
    @ApiResponse(responseCode = "200", description = "Liste erfolgreich geladen")
    @GetMapping
    public ResponseEntity<List<OwnedPokemonDTO>> getAllPokemon() {
        var dtos =  ownedService.getAllPokemon()
                .stream()
                .map(OwnedPokemonDTO::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }


    /**
     * Lädt ein einzelnes Pokemon anhand seiner ID
     * @param id Die ID des zu suchenden Pokemon
     * @return Das gefundene Pokemon
     */
    @Operation(summary = "Holt das gesuchte Pokemon anhand dessen ID", description = "Gibt das gefangene Pokemon mit allen Eigenschaften zurück")
    @ApiResponse(responseCode = "200", description = "Pokemon gefunden")
    @ApiResponse(responseCode = "404", description = "Pokemon nicht gefunden", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<OwnedPokemonDTO> getPokemonById(@PathVariable Long id) {
        try {
            OwnedPokemon pokemon = ownedService.getPokemonById(id);
            return ResponseEntity.ok(OwnedPokemonDTO.from(pokemon));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Fügt ein neues Pokemon hinzu
     * @param request Die Eingabedaten (ID, Nicknamen, Level, Box, Edition)
     * @return Das neue hinzugefügte Pokemon
     */
    @Operation(summary = "Fügt ein neues gefangenes Pokemon hinzu", description = "Wichtiger Endpunkt zum Erfassen eines neuen Pokemon")
    @ApiResponse(responseCode = "201", description = "Pokemon erfolgreich hinzugefügt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabe Daten", content = @Content)
    @ApiResponse(responseCode = "409", description = "Ziel Box voll oder ungültige Entwicklung", content = @Content)
    @PostMapping
    public ResponseEntity<OwnedPokemonDTO> addPokemon(@RequestBody @Valid CreateOwnedDTO request) {
        var pokemon = ownedService.addPokemon(request);
        return ResponseEntity.status(201).body(OwnedPokemonDTO.from(pokemon));
    }


    /**
     * Aktualisiert ein bereits vorhandenes Pokemon
     * @param id Die ID des zu aktualisierenden Pokemon
     * @param request Die neue Pokemon Werte
     * @return Das aktualisierte Pokemon
     */
    @Operation(summary = "Aktualisiert ein vorhanden gefangenen Pokemon", description = "Bearbeitet alle Eigenschaften eines Pokemon")
    @ApiResponse(responseCode = "200", description = "Pokemon wurde aktualisiert")
    @ApiResponse(responseCode = "400", description = "Aktualisierung fehlgeschlagen", content = @Content)
    @ApiResponse(responseCode = "404", description = "Pokemon nicht gefunden", content = @Content)
    @PatchMapping("/{id}")
    public ResponseEntity<OwnedPokemonDTO> updatePokemon(@PathVariable Long id, @RequestBody @Valid UpdateOwnedDTO request) {
        var updated = ownedService.updatePokemon(id, request);
        return ResponseEntity.ok(OwnedPokemonDTO.from(updated));
    }


    /**
     * Löscht das Pokemon anhand der ID
     * @param id Die ID des zu löschenden Pokemon
     */
    @Operation(summary = "Entfernt das Pokemon aus dem Speicher", description = "löscht das Pokemon dauerhaft")
    @ApiResponse(responseCode = "200", description = "Pokemon erfolgreich gelöscht")
    @ApiResponse( responseCode = "404", description = "Pokemon nicht gefunden")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePokemonById(@PathVariable Long id) {
        ownedService.deletePokemonById(id);
        return ResponseEntity.noContent().build();
    }
}
