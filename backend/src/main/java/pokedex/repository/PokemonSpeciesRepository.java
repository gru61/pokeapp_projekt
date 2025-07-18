package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokedex.model.PokemonSpecies;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für Datenbankzugriffe auf {@link PokemonSpecies}-Entitäten.
 * <p>
 * Erweitert das Spring Data {@link JpaRepository} und bietet neben den Standard-CRUD-Operationen
 * zwei spezielle Suchmethoden für Pokédex-ID und Name.
 * </p>
 *
 * <b>Typische Verwendung:</b>
 * <ul>
 *   <li>Laden einer Pokémon-Art über die Pokédex-ID ({@link #findByPokedexId(int)})</li>
 *   <li>Suchen von Pokémon-Arten anhand ihres Namens ({@link #findByName(String)})</li>
 *   <li>Allgemeine Datenbankoperationen für Species (z.B. für den Pokédex-Dialog)</li>
 * </ul>
 *
 * <b>Besonderheiten:</b>
 * <ul>
 *   <li>{@link #findByPokedexId(int)} liefert ein {@link Optional}, falls die ID nicht existiert</li>
 *   <li>{@link #findByName(String)} liefert eine Liste aller Arten mit passendem Namen (üblicherweise ein oder null Treffer)</li>
 * </ul>
 *
 * @author grubi
 */
@Repository
public interface PokemonSpeciesRepository extends JpaRepository<PokemonSpecies, Long> {

    /**
     * Sucht eine Pokémon-Art anhand ihrer Pokédex-ID.
     *
     * @param pokedexId Pokédex-ID der gesuchten Art
     * @return Optional mit der gefundenen Art, oder leer falls nicht vorhanden
     */
    Optional<PokemonSpecies> findByPokedexId(int pokedexId);

    /**
     * Sucht alle Pokémon-Arten mit einem bestimmten Namen.
     * Kann theoretisch mehrere Treffer liefern (z.B. bei Namensduplikaten).
     *
     * @param name Name der gesuchten Art
     * @return Liste aller passenden Pokémon-Arten
     */
    List<PokemonSpecies> findByName(String name);
}
