package pokedex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pokedex.model.Box;
import pokedex.model.BoxName;
import pokedex.model.Edition;

import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Long> {
    Optional<Box> findByNameAndEdition(BoxName name, Edition edition);

}
