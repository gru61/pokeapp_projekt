package pokedex.exception;

/**
 * Exception, die geworfen wird, wenn eine angeforderte Ressource (z.B. Pokémon-Art, Box, eigenes Pokémon)
 * mit einer bestimmten ID oder einem bestimmten Namen nicht gefunden werden konnte.
 * <p>
 * Typischerweise eingesetzt in Service- oder Controller-Logik, um dem Client mitzuteilen,
 * dass eine Entität nicht existiert (z.B. beim Suchen oder Löschen).
 * </p>
 *
 * <b>Typische Anwendungsfälle:</b>
 * <ul>
 *   <li>Pokémon-Art mit bestimmter Pokédex-ID existiert nicht</li>
 *   <li>Box mit gewünschtem Namen/ID nicht vorhanden</li>
 *   <li>Eigenes Pokémon mit ID wurde nicht gefunden</li>
 * </ul>
 *
 * Wird vom {@link pokedex.exception.GlobalExceptionHandler} abgefangen und als HTTP-404 (Not Found) zurückgegeben.
 *
 * @author grubi
 */
public class NotFoundException extends RuntimeException {

    /**
     * Erstellt eine neue NotFoundException mit individueller Fehlermeldung.
     * @param message Beschreibung des Fehlers (z.B. "Pokémon mit ID 99 nicht gefunden")
     */
    public NotFoundException(String message) {
        super(message);
    }
}
