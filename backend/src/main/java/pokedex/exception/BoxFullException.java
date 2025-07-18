package pokedex.exception;

/**
 * Exception, die geworfen wird, wenn eine Box ihr Kapazitätslimit erreicht hat.
 * <p>
 * Wird z.B. ausgelöst, wenn beim Hinzufügen oder Verschieben eines Pokémon
 * versucht wird, eine bereits volle Box (z.B. Team = 6, andere Box = 20) weiter zu befüllen.
 * <br>
 * Die Exception kann vom Controller abgefangen werden, um dem Nutzer einen passenden Fehlercode oder Hinweis zu liefern.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Beim Hinzufügen eines neuen Pokémon in eine volle Box</li>
 *   <li>Beim Drag & Drop-Verschieben in eine volle Ziel-Box</li>
 * </ul>
 *
 * @author grubi
 */
public class BoxFullException extends RuntimeException {

    /**
     * Konstruktor für eine neue BoxFullException mit eigener Fehlermeldung.
     * @param message Detaillierte Fehlerbeschreibung für Log oder Nutzer
     */
    public BoxFullException(String message) {
        super(message);
    }
}
