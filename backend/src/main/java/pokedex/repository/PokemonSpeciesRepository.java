package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokedex.model.PokemonSpecies;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonSpeciesRepository extends JpaRepository<PokemonSpecies, Long> {
    Optional<PokemonSpecies> findByPokedexId(int pokedexId);
    List<PokemonSpecies> findByName(String name);
}
