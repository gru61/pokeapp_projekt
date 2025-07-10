package pokedex.exception;

/**
 * Wird geworfen, wenn eine Box ihr Kapazitätslimit erreicht hat.
 */
public class BoxFullException extends RuntimeException {
    public BoxFullException(String message) {
        super(message);
    }
}
