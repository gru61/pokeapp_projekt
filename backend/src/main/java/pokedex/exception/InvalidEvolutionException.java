package pokedex.exception;

/**
 * Exception, die geworfen wird, wenn eine Entwicklung eines Pokémon nicht zulässig ist.
 * <p>
 * Wird beispielsweise ausgelöst, wenn versucht wird, eine nicht erlaubte oder nicht definierte Entwicklung
 * durchzuführen (z.B. Glumanda → Enton, oder Entwicklung zu einer Art außerhalb der Evolutionstabelle).
 * </p>
 *
 * <b>Typische Anwendungsfälle:</b>
 * <ul>
 *   <li>Ungültige Entwicklung im Frontend oder Backend</li>
 *   <li>Fehlerhafte Bearbeitung von Evolutionen, z.B. beim PATCH-Request</li>
 *   <li>Entwicklung, die gegen die spezifizierten Regeln oder Konfigurationen verstößt</li>
 * </ul>
 *
 * Diese Exception kann im {@link pokedex.exception.GlobalExceptionHandler} abgefangen werden,
 * um dem Nutzer eine sinnvolle Fehlermeldung auszugeben.
 *
 * @author grubi
 */
public class InvalidEvolutionException extends RuntimeException {

    /**
     * Konstruktor für eine neue InvalidEvolutionException mit eigener Fehlermeldung.
     * @param message Detaillierte Fehlerbeschreibung
     */
    public InvalidEvolutionException(String message) {
        super(message);
    }
}
