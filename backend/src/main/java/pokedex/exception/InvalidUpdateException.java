package pokedex.exception;

/**
 * Exception, die ausgelöst wird, wenn ein Update-Vorgang gegen Spielregeln oder fachliche Einschränkungen verstößt.
 * <p>
 * Beispiele sind: Versuch, das Level eines Pokémon zu verringern, unerlaubter Wechsel der Edition oder
 * andere Änderungen, die durch Geschäftslogik untersagt sind.
 * </p>
 *
 * <b>Typische Anwendungsfälle:</b>
 * <ul>
 *   <li>Level soll gesenkt werden (was laut Regeln nicht erlaubt ist)</li>
 *   <li>Edition darf nicht beliebig geändert werden</li>
 *   <li>Weitere Prüfungen und fachliche Regeln in Service- oder Controller-Logik</li>
 * </ul>
 *
 * Die Exception sollte vom {@link pokedex.exception.GlobalExceptionHandler} abgefangen werden,
 * um dem Nutzer eine verständliche Rückmeldung zu liefern.
 *
 * @author grubi
 */
public class InvalidUpdateException extends RuntimeException {

    /**
     * Erstellt eine neue InvalidUpdateException mit individueller Fehlermeldung.
     * @param message Detaillierte Fehlerbeschreibung für Log oder Nutzer
     */
    public InvalidUpdateException(String message) {
        super(message);
    }
}
