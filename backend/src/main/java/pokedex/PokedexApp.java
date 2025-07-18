package pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse zum Starten der gesamten Anwendung (Spring Boot Backend).
 * <p>
 * Initialisiert die Spring-Umgebung und startet den eingebetteten Server.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Lokaler Entwicklungsstart ("main"-Methode)</li>
 *   <li>Produktivsysteme (z.B. als Executable Jar)</li>
 * </ul>
 *
 * <b>Hinweis:</b>
 * Alle Komponenten (Controller, Services, Repositories etc.) werden über Spring Boot automatisch erkannt und gestartet.
 *
 * @author grubi
 */
@SpringBootApplication
public class PokedexApp {

    /**
     * Einstiegspunkt der Anwendung.
     * Startet das Spring Boot Backend für den Pokédex.
     *
     * @param args Kommandozeilenargumente (werden i.d.R. nicht genutzt)
     */
    public static void main(String[] args) {
        SpringApplication.run(PokedexApp.class, args);
    }

}
