package pokedex.exception;

/**
 * Wird geworfen, wenn versucht wird, ein Pokémon in dieselbe Box zu verschieben.
 */
public class SameBoxException extends RuntimeException {
    public SameBoxException(String message) {
        super(message);
    }
}