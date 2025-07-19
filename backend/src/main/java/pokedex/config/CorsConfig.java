package pokedex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Konfiguriert CORS (Cross-Origin Resource Sharing) für die Webanwendung.
 * Typischer Anwendungsfall:
 * Die Konfiguration erlaubt es, dass dein Frontend (z. B. React/Vite-App) mit dem Backend kommunizieren darf,
 * auch wenn sie auf unterschiedlichen Hosts oder Ports läuft.
 * Erlaubte HTTP-Methoden:
 * <ul>
 *     <li>GET</li>
 *     <li>POST</li>
 *     <li>PUT</li>
 *     <li>DELETE</li>
 *     <li>PATCH</li>
 *     <li>OPTIONS</li>
 * </ul>
 * @author grubi
 */


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Fügt die CORS-Konfiguration für die Anwendung hinzu.
     *
     * @param registry das CorsRegistry-Objekt, dem die Mappings hinzugefügt werden
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Type", "Authorization")
                .allowCredentials(false)
                .maxAge(3600);
    }
}