package pokedex.exception;

/**
 * Wird geworfen, wenn eine Pokemon-Art mit einer bestimmten ID nicht gefunden wurde.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
