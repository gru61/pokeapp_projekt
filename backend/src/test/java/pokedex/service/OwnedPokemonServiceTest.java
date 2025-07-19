package pokedex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pokedex.dto.CreateOwnedDTO;
import pokedex.dto.UpdateOwnedDTO;
import pokedex.exception.BoxFullException;
import pokedex.exception.NotFoundException;
import pokedex.model.*;
import pokedex.repository.OwnedPokemonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnedPokemonServiceTest {

    private OwnedPokemonRepository ownedRepo;
    private PokemonSpeciesService speciesService;
    private BoxService boxService;
    private EvolutionService evolutionService;
    private OwnedPokemonService ownedService;

    @BeforeEach
    void setup() {
        ownedRepo = mock(OwnedPokemonRepository.class);
        speciesService = mock(PokemonSpeciesService.class);
        boxService = mock(BoxService.class);
        evolutionService = mock(EvolutionService.class);
        ownedService = new OwnedPokemonService(ownedRepo, speciesService, boxService, evolutionService);
    }

    @Test
    void testAddPokemon_successful() {
        // Arrange
        CreateOwnedDTO dto = new CreateOwnedDTO();
        dto.setPokedexId(25); // z.B. Pikachu
        dto.setLevel(7);
        dto.setEdition(Edition.ROT);
        dto.setBox(BoxName.BOX1);
        dto.setNickname("Testchu");

        PokemonSpecies species = new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null);
        Box box = new Box(BoxName.BOX1, Edition.ROT);

        when(speciesService.getByPokedexId(25)).thenReturn(Optional.of(species));
        when(boxService.isFull(BoxName.BOX1, Edition.ROT)).thenReturn(false);
        when(boxService.getBoxByNameAndEdition(BoxName.BOX1, Edition.ROT)).thenReturn(box);

        OwnedPokemon expected = new OwnedPokemon(species, "Testchu", 7, Edition.ROT, box);
        when(ownedRepo.save(any(OwnedPokemon.class))).thenReturn(expected);

        // Act
        OwnedPokemon result = ownedService.addPokemon(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Testchu", result.getNickname());
        assertEquals(25, result.getSpecies().getPokedexId());
        verify(ownedRepo).save(any(OwnedPokemon.class));
    }

    @Test
    void testAddPokemon_boxFull_throwsBoxFullException() {
        // Arrange
        CreateOwnedDTO dto = new CreateOwnedDTO();
        dto.setPokedexId(25);
        dto.setLevel(7);
        dto.setEdition(Edition.ROT);
        dto.setBox(BoxName.TEAM);

        PokemonSpecies species = new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null);

        when(speciesService.getByPokedexId(25)).thenReturn(Optional.of(species));
        when(boxService.isFull(BoxName.TEAM, Edition.ROT)).thenReturn(true);

        // Act & Assert
        assertThrows(BoxFullException.class, () -> ownedService.addPokemon(dto));
    }

    @Test
    void testAddPokemon_speciesNotFound_throwsNotFoundException() {
        // Arrange
        CreateOwnedDTO dto = new CreateOwnedDTO();
        dto.setPokedexId(999); // nicht existent
        dto.setLevel(5);
        dto.setEdition(Edition.BLAU);
        dto.setBox(BoxName.BOX2);

        when(speciesService.getByPokedexId(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> ownedService.addPokemon(dto));
    }

    @Test
    void testUpdatePokemon_successfulNicknameUpdate() {
        // Arrange
        OwnedPokemon existing = mock(OwnedPokemon.class);
        when(existing.getLevel()).thenReturn(12);
        when(existing.getBox()).thenReturn(new Box(BoxName.BOX1, Edition.ROT));
        when(existing.getEdition()).thenReturn(Edition.ROT);
        when(existing.getSpecies()).thenReturn(new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null));
        when(ownedRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(ownedRepo.save(any())).thenReturn(existing);

        UpdateOwnedDTO update = UpdateOwnedDTO.builder()
                .nickname("NeuerName")
                .level(12)
                .box(BoxName.BOX1)
                .edition(Edition.ROT)
                .pokedexId(25)
                .build();

        OwnedPokemon updated = ownedService.updatePokemon(1L, update);

        assertNotNull(updated);
        verify(existing).setNickname("NeuerName");
        verify(ownedRepo).save(existing);
    }

    @Test
    void testUpdatePokemon_levelDecrease_throwsException() {
        // Arrange
        OwnedPokemon existing = mock(OwnedPokemon.class);
        when(existing.getLevel()).thenReturn(10);
        when(existing.getBox()).thenReturn(new Box(BoxName.BOX1, Edition.ROT));
        when(existing.getEdition()).thenReturn(Edition.ROT);
        when(existing.getSpecies()).thenReturn(new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null));
        when(ownedRepo.findById(1L)).thenReturn(Optional.of(existing));

        UpdateOwnedDTO update = UpdateOwnedDTO.builder()
                .nickname("Name")
                .level(5) // Level sinkt!
                .box(BoxName.BOX1)
                .edition(Edition.ROT)
                .pokedexId(25)
                .build();

        assertThrows(pokedex.exception.InvalidUpdateException.class, () -> ownedService.updatePokemon(1L, update));
    }
}
