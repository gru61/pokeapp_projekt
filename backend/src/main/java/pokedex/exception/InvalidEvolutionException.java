package pokedex.exception;


/**
 * Wird geworfen, wenn eine Entwicklung nicht zulässig ist (z.B. Glumanda → Enton).
 */
public class InvalidEvolutionException extends RuntimeException {
    public InvalidEvolutionException(String message) {
        super(message);
    }
}