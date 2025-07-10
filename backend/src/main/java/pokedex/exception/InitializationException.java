package pokedex.exception;


/**
 * Wird geworfen, wenn beim Laden der Konfiguration (z.B. JSON-Dateien) ein Fehler auftritt.
 */
public class InitializationException extends RuntimeException {
    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
