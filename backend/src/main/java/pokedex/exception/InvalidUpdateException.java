package pokedex.exception;

/**
 * Wird geworfen, wenn ein Update (z.B. Änderung von Level, Edition etc.) gegen Spielregeln verstösst.
 */
public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(String message) {
        super(message);
    }
}