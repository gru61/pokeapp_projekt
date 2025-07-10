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
 * Globale Fehlerbehandlung für die gesamte REST-API.
 * Fängt Validierungsfehler und benutzerdefinierte Exceptions ab und gibt strukturierte JSON-Antworten zurück.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Fängt Validierungsfehler von DTOs ab (@Valid)
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

    // Fängt ConstraintViolationExceptions ab (z.B. bei Parametern direkt in der URL)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return errorResponse("Ungültige Eingabe: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Box ist voll
    @ExceptionHandler(BoxFullException.class)
    public ResponseEntity<Object> handleBoxFull(BoxFullException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Objekt (z.B. Pokémon-Art) nicht gefunden
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Ungültiges Update (z.B. Level gesenkt, gleiche Box)
    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<Object> handleInvalidUpdate(InvalidUpdateException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Gleiches Ziel bei Verschieben
    @ExceptionHandler(SameBoxException.class)
    public ResponseEntity<Object> handleSameBox(SameBoxException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Allgemeine Zustandsfehler
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Unerwarteter Fehler (optional, aber sinnvoll)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleUnhandled(RuntimeException ex) {
        return errorResponse("Ein unerwarteter Fehler ist aufgetreten: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Hilfsmethode für Standard-Fehlermeldung mit Zeitstempel
    private ResponseEntity<Object> errorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    // Wird geworfen, wenn eine Entwicklung nicht zulässig ist
    @ExceptionHandler(InvalidEvolutionException.class)
    public ResponseEntity<Object> handleInvalidEvolution(InvalidEvolutionException ex) {
        return errorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Wird geworfen, wenn die JSON-Konfiguration nicht geladen werden kann
    @ExceptionHandler(InitializationException.class)
    public ResponseEntity<Object> handleInitialization(InitializationException ex) {
        return errorResponse("Fehler bei der Initialisierung: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
