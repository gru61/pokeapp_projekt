package pokedex.exception;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Globale Fehlerbehandlung für alle REST-Endpunkte der Anwendung.
 * <p>
 * Diese Klasse fängt alle in der Anwendung nicht behandelten Exceptions ab und sorgt dafür,
 * dass das Frontend immer eine klar strukturierte, einheitliche und lesbare JSON-Antwort erhält –
 * statt eines generischen Fehlerstacks.
 * </p>
 *
 * <b>Funktionen und Besonderheiten:</b>
 * <ul>
 *   <li>Behandelt Validierungsfehler (z.B. von DTOs mit {@code @Valid}) mit Angabe aller betroffenen Felder.</li>
 *   <li>Fängt gezielt alle eigenen Custom-Exceptions ab (z.B. {@link BoxFullException}, {@link NotFoundException}).</li>
 *   <li>Gibt zu jedem Fehler einen Zeitstempel und die genaue Fehlermeldung zurück.</li>
 *   <li>Unterteilt die Fehler nach sinnvollen HTTP-Statuscodes (400, 404, 409, 500 ...).</li>
 *   <li>Hilfsmethode <code>errorResponse</code> erzeugt das JSON-Objekt für die Antwort.</li>
 * </ul>
 *
 * <b>Typische Fehlerarten:</b>
 * <ul>
 *   <li>Validierungsfehler bei DTOs (Bad Request, mit Feldliste)</li>
 *   <li>Constraint-Verletzungen (z.B. URL-Parameter)</li>
 *   <li>Box voll, Entwicklung unzulässig, Zustand ungültig etc.</li>
 *   <li>Objekte nicht gefunden (404)</li>
 *   <li>Allgemeiner, unerwarteter Fehler (500)</li>
 * </ul>
 *
 * <b>Beispiel für eine Fehlerantwort:</b>
 * <pre>
 * {
 *   "timestamp": "2024-07-18T13:45:12.527",
 *   "message": "Ziel-Box ist voll"
 * }
 * </pre>
 *
 * @author grubi
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Behandelt Validierungsfehler bei DTOs (z.B. aus {@code @Valid}).
     * Gibt alle betroffenen Felder und deren Fehlermeldung als Map zurück.
     *
     * @param ex      Die geworfene Exception
     * @param headers HTTP-Header
     * @param status  HTTP-Statuscode
     * @param request WebRequest-Objekt
     * @return ResponseEntity mit Map "Feldname" → "Fehlermeldung", Status 400
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Fängt {@link ConstraintViolationException}s ab (z.B. fehlerhafte Parameter in der URL).
     * @param ex Die geworfene Exception
     * @return Standardisierte Fehlermeldung mit Status 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return errorResponse("Ungültige Eingabe: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Fängt Fälle ab, in denen eine Box voll ist.
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 409 (Conflict)
     */
    @ExceptionHandler(BoxFullException.class)
    public ResponseEntity<Object> handleBoxFull(BoxFullException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Behandelt Fälle, in denen ein Objekt (z.B. Pokémon) nicht gefunden wurde.
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 404
     */
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Behandelt ungültige Updates (z.B. Level gesenkt, gleiche Box).
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 400
     */
    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<Object> handleInvalidUpdate(InvalidUpdateException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Wird ausgelöst, wenn beim Verschieben das Ziel identisch zur Quelle ist.
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 409 (Conflict)
     */
    @ExceptionHandler(SameBoxException.class)
    public ResponseEntity<Object> handleSameBox(SameBoxException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Behandelt allgemeine Zustandsfehler.
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Behandelt unerwartete Fehler, die nicht explizit abgefangen wurden.
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 500
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleUnhandled(RuntimeException ex) {
        return errorResponse("Ein unerwarteter Fehler ist aufgetreten: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Hilfsmethode zur Erzeugung der Fehlerantwort mit Zeitstempel und Meldung.
     * @param message Fehlerbeschreibung
     * @param status HTTP-Statuscode
     * @return ResponseEntity mit Map (timestamp, message)
     */
    private ResponseEntity<Object> errorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    /**
     * Behandelt ungültige Entwicklungen (z.B. nicht erlaubte Entwicklung).
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 400
     */
    @ExceptionHandler(InvalidEvolutionException.class)
    public ResponseEntity<Object> handleInvalidEvolution(InvalidEvolutionException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Behandelt Fehler beim Laden der Initialkonfiguration (z.B. JSON-Probleme).
     * @param ex Die geworfene Exception
     * @return Fehlerantwort mit Status 500
     */
    @ExceptionHandler(InitializationException.class)
    public ResponseEntity<Object> handleInitialization(InitializationException ex) {
        return errorResponse("Fehler bei der Initialisierung: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
