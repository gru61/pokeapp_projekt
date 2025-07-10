package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pokedex.model.Box;
import pokedex.model.OwnedPokemon;


@Repository
public interface OwnedPokemonRepository extends JpaRepository<OwnedPokemon, Long> {

    @Query("select count(p) from OwnedPokemon p where p.box = :box")
    Long countByBox(@Param("box")Box box);
}
