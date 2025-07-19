package pokedex.exception;

/**
 * Exception, die geworfen wird, wenn versucht wird, ein Pokémon in dieselbe Box zu verschieben,
 * in der es sich bereits befindet.
 * <p>
 * Diese Exception wird z.B. ausgelöst, wenn der Nutzer im UI per Drag & Drop keine tatsächliche
 * Veränderung bewirkt (Quelle und Ziel identisch). Die Geschäftslogik verhindert damit unnötige
 * oder fehlerhafte Operationen.
 * </p>
 *
 * <b>Typische Anwendungsfälle:</b>
 * <ul>
 *   <li>Drag & Drop eines Pokémon in die aktuell gleiche Box (mit oder ohne Edition)</li>
 *   <li>Verschiebe-Requests, bei denen Quell- und Zielbox (und ggf. Edition) identisch sind</li>
 * </ul>
 *
 * Die Exception wird vom {@link pokedex.exception.GlobalExceptionHandler} abgefangen
 * und typischerweise mit HTTP 409 (Conflict) beantwortet.
 *
 * @author grubi
 */
public class SameBoxException extends RuntimeException {

    /**
     * Erstellt eine neue SameBoxException mit individueller Fehlermeldung.
     * @param message Beschreibung des Fehlers (z.B. "Zielbox und Quellbox sind identisch")
     */
    public SameBoxException(String message) {
        super(message);
    }
}
