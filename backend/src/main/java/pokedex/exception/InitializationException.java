package pokedex.exception;

/**
 * Exception, die ausgelöst wird, wenn beim Initialisieren der Anwendung ein Fehler auftritt.
 * <p>
 * Typische Anwendungsfälle sind Probleme beim Laden von Konfigurationsdateien (z.B. JSON, SQL, Ressourcen),
 * oder wenn eine Initialisierung im {@code DataLoader} oder anderen Startprozessen fehlschlägt.
 * </p>
 *
 * <b>Beispiele:</b>
 * <ul>
 *   <li>Lesefehler bei der Initial-Pokédex-Datei</li>
 *   <li>Ungültiges oder unvollständiges Konfigurationsformat</li>
 *   <li>Fehlende Pflichtdaten beim Start</li>
 * </ul>
 *
 * Diese Exception sollte möglichst früh im Stack gefangen und
 * im {@link pokedex.exception.GlobalExceptionHandler} an das Frontend gemeldet werden.
 *
 * @author grubi
 */
public class InitializationException extends RuntimeException {

    /**
     * Erstellt eine neue InitializationException mit einer eigenen Fehlermeldung.
     * @param message Detaillierte Fehlerbeschreibung
     */
    public InitializationException(String message) {
        super(message);
    }

    /**
     * Erstellt eine neue InitializationException mit Meldung und Ursache.
     * @param message Fehlerbeschreibung
     * @param cause   Ursprüngliche Exception
     */
    public InitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
