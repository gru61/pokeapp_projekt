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
 * REST-Controller zur Verwaltung aller gefangenen Pokémon (Owned).
 * <p>
 * Bietet CRUD-Endpunkte zum Laden, Hinzufügen, Aktualisieren und Löschen von Pokémon, die der Nutzer gefangen hat.
 * Alle Rückgaben und Eingaben erfolgen mithilfe von DTOs, die das Domänenmodell vom API-Modell trennen.
 * </p>
 *
 * <b>Typische Nutzung:</b>
 * <ul>
 *     <li>Laden der eigenen Pokémon-Liste (z. B. für Übersicht/Inventar)</li>
 *     <li>Hinzufügen eines neu gefangenen Pokémon</li>
 *     <li>Aktualisieren von Eigenschaften (Nickname, Level, Box, Edition, ...)</li>
 *     <li>Löschen eines Pokémon</li>
 * </ul>
 *
 * @author grubi
 */
@RestController
@RequestMapping("/api/pokemon")
public class OwnedPokemonController {

    /** Service zur Verwaltung aller eigenen Pokémon. */
    private final OwnedPokemonService ownedService;

    /**
     * Konstruktor für Dependency Injection.
     * @param ownedService Der zu verwendende Service für Owned-Pokémon
     */
    public OwnedPokemonController(OwnedPokemonService ownedService) {
        this.ownedService = ownedService;
    }

    /**
     * Gibt die vollständige Liste aller gefangenen Pokémon zurück.
     * <p>
     * Wird typischerweise zum Laden der Übersichtsseite genutzt.
     *
     * @return Liste aller eigenen Pokémon als {@link OwnedPokemonDTO}
     */
    @Operation(summary = "Lädt alle gefangenen Pokémon",
            description = "Gibt eine Liste aller gespeicherten eigenen Pokémon zurück")
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
     * Lädt ein einzelnes gefangenes Pokémon anhand seiner ID.
     * <p>
     * Wird verwendet für Detailansicht oder gezielte Aktionen im UI.
     *
     * @param id Die ID des gesuchten Pokémon
     * @return Das gefundene Pokémon als {@link OwnedPokemonDTO} oder 404, falls nicht vorhanden
     */
    @Operation(summary = "Holt ein gefangenes Pokémon anhand der ID",
            description = "Gibt das gefangene Pokémon mit allen Eigenschaften zurück")
    @ApiResponse(responseCode = "200", description = "Pokémon gefunden")
    @ApiResponse(responseCode = "404", description = "Pokémon nicht gefunden", content = @Content)
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
     * Fügt ein neues eigenes Pokémon hinzu.
     * <p>
     * Nutzt ein DTO für die Eingabe. Prüft automatisch die Eingabedaten (z. B. Pflichtfelder, Level, Box).
     * Bei Fehlern (Box voll, ungültige Entwicklung, ungültige Daten) werden entsprechende Statuscodes zurückgegeben.
     *
     * @param request Die Eingabedaten für das neue Pokémon (siehe {@link CreateOwnedDTO})
     * @return Das neu erstellte Pokémon als {@link OwnedPokemonDTO}, Status 201 (Created) bei Erfolg
     */
    @Operation(summary = "Fügt ein neues eigenes Pokémon hinzu",
            description = "Wichtiger Endpunkt zum Erfassen eines neu gefangenen Pokémon")
    @ApiResponse(responseCode = "201", description = "Pokémon erfolgreich hinzugefügt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten", content = @Content)
    @ApiResponse(responseCode = "409", description = "Zielbox voll oder ungültige Entwicklung", content = @Content)
    @PostMapping
    public ResponseEntity<OwnedPokemonDTO> addPokemon(@RequestBody @Valid CreateOwnedDTO request) {
        var pokemon = ownedService.addPokemon(request);
        return ResponseEntity.status(201).body(OwnedPokemonDTO.from(pokemon));
    }

    /**
     * Aktualisiert ein bestehendes Pokémon anhand seiner ID.
     * <p>
     * Alle Eigenschaften können bearbeitet werden (Nickname, Level, Box, Edition, ...).
     * Validierung erfolgt serverseitig. Bei Fehlern entsprechende Statuscodes.
     *
     * @param id      Die ID des zu aktualisierenden Pokémon
     * @param request Die neuen Eigenschaften (siehe {@link UpdateOwnedDTO})
     * @return Das aktualisierte Pokémon als {@link OwnedPokemonDTO}
     */
    @Operation(summary = "Aktualisiert ein gefangenes Pokémon",
            description = "Bearbeitet alle Eigenschaften eines gefangenen Pokémon")
    @ApiResponse(responseCode = "200", description = "Pokémon wurde aktualisiert")
    @ApiResponse(responseCode = "400", description = "Aktualisierung fehlgeschlagen", content = @Content)
    @ApiResponse(responseCode = "404", description = "Pokémon nicht gefunden", content = @Content)
    @PatchMapping("/{id}")
    public ResponseEntity<OwnedPokemonDTO> updatePokemon(@PathVariable Long id, @RequestBody @Valid UpdateOwnedDTO request) {
        var updated = ownedService.updatePokemon(id, request);
        return ResponseEntity.ok(OwnedPokemonDTO.from(updated));
    }

    /**
     * Löscht ein gefangenes Pokémon anhand seiner ID.
     * <p>
     * Achtung: Der Löschvorgang ist endgültig und nicht reversibel!
     *
     * @param id Die ID des zu löschenden Pokémon
     * @return Status 204 (No Content) bei Erfolg; 404, falls nicht vorhanden
     */
    @Operation(summary = "Entfernt ein gefangenes Pokémon aus dem Speicher",
            description = "Löscht das Pokémon dauerhaft (nicht wiederherstellbar!)")
    @ApiResponse(responseCode = "204", description = "Pokémon erfolgreich gelöscht")
    @ApiResponse(responseCode = "404", description = "Pokémon nicht gefunden")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePokemonById(@PathVariable Long id) {
        ownedService.deletePokemonById(id);
        return ResponseEntity.noContent().build();
    }
}
