package pokedex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import pokedex.exception.BoxFullException;
import pokedex.exception.NotFoundException;
import pokedex.exception.SameBoxException;
import pokedex.model.*;
import pokedex.repository.BoxRepository;
import pokedex.repository.OwnedPokemonRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoxServiceTest {

    private BoxRepository boxRepo;
    private OwnedPokemonRepository ownedRepo;
    private BoxService boxService;

    @BeforeEach
    void setup() {
        boxRepo = mock(BoxRepository.class);
        ownedRepo = mock(OwnedPokemonRepository.class);
        boxService = new BoxService(boxRepo, ownedRepo);
    }

    @Test
    void testGetBoxByNameAndEdition_boxFound() {
        Box box = new Box(BoxName.BOX1, Edition.ROT);
        when(boxRepo.findByNameAndEdition(BoxName.BOX1, Edition.ROT)).thenReturn(Optional.of(box));
        Box result = boxService.getBoxByNameAndEdition(BoxName.BOX1, Edition.ROT);
        assertEquals(box, result);
    }

    @Test
    void testGetBoxByNameAndEdition_boxNotFound() {
        when(boxRepo.findByNameAndEdition(BoxName.BOX2, Edition.BLAU)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> boxService.getBoxByNameAndEdition(BoxName.BOX2, Edition.BLAU));
    }

    @Test
    void testIsFull_returnsTrueIfBoxFull_team() {
        Box box = new Box(BoxName.TEAM, Edition.ROT);
        when(boxRepo.findByNameAndEdition(BoxName.TEAM, Edition.ROT)).thenReturn(Optional.of(box));
        when(ownedRepo.countByBox(box)).thenReturn(6L);
        assertTrue(boxService.isFull(BoxName.TEAM, Edition.ROT));
    }

    @Test
    void testIsFull_returnsFalseIfBoxNotFull_box() {
        Box box = new Box(BoxName.BOX1, Edition.BLAU);
        when(boxRepo.findByNameAndEdition(BoxName.BOX1, Edition.BLAU)).thenReturn(Optional.of(box));
        when(ownedRepo.countByBox(box)).thenReturn(5L);
        assertFalse(boxService.isFull(BoxName.BOX1, Edition.BLAU));
    }

    @Test
    void testMovePokemon_sameBox_throwsSameBoxException() {
        assertThrows(SameBoxException.class, () -> boxService.movePokemon(
                1L, BoxName.BOX1, Edition.ROT, BoxName.BOX1, Edition.ROT));
    }

    @Test
    void testMovePokemon_pokemonNotFound_throwsNotFoundException() {
        when(ownedRepo.findById(42L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> boxService.movePokemon(
                42L, BoxName.BOX1, Edition.ROT, BoxName.BOX2, Edition.BLAU));
    }

    @Test
    void testMovePokemon_boxFull_throwsBoxFullException() {
        OwnedPokemon pokemon = mock(OwnedPokemon.class);
        Box box = new Box(BoxName.BOX1, Edition.ROT);
        when(pokemon.getBox()).thenReturn(box);
        when(pokemon.getEdition()).thenReturn(Edition.ROT);
        when(ownedRepo.findById(1L)).thenReturn(Optional.of(pokemon));

        Box targetBox = new Box(BoxName.BOX2, Edition.BLAU);
        when(boxRepo.findByNameAndEdition(BoxName.BOX2, Edition.BLAU)).thenReturn(Optional.of(targetBox));
        when(ownedRepo.countByBox(targetBox)).thenReturn(20L); // Box ist voll!

        assertThrows(BoxFullException.class, () -> boxService.movePokemon(
                1L, BoxName.BOX1, Edition.ROT, BoxName.BOX2, Edition.BLAU));
    }

    @Test
    void testMovePokemon_successfulMove() {
        // Arrange
        // Pokémon in Box1/Rot
        OwnedPokemon pokemon = mock(OwnedPokemon.class);
        Box sourceBox = new Box(BoxName.BOX1, Edition.ROT);
        Box targetBox = new Box(BoxName.BOX2, Edition.BLAU);

        when(pokemon.getBox()).thenReturn(sourceBox);
        when(pokemon.getEdition()).thenReturn(Edition.ROT);
        when(ownedRepo.findById(1L)).thenReturn(Optional.of(pokemon));

        when(boxRepo.findByNameAndEdition(BoxName.BOX2, Edition.BLAU)).thenReturn(Optional.of(targetBox));
        when(ownedRepo.countByBox(targetBox)).thenReturn(0L); // Box NICHT voll

        // Act
        assertDoesNotThrow(() -> boxService.movePokemon(
                1L, BoxName.BOX1, Edition.ROT, BoxName.BOX2, Edition.BLAU));

        // Assert
        // Das Pokémon sollte nun die Zielbox und Zieledition bekommen haben
        verify(pokemon).setBox(targetBox);
        verify(pokemon).setEdition(Edition.BLAU);
        // Pokémon wurde gespeichert
        verify(ownedRepo).save(pokemon);
    }
}
