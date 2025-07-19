package pokedex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pokedex.dto.CreateOwnedDTO;
import pokedex.dto.OwnedPokemonDTO;
import pokedex.exception.NotFoundException;
import pokedex.model.*;
import pokedex.service.OwnedPokemonService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnedPokemonController.class)
class OwnedPokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnedPokemonService ownedService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPokemon_returnsList() throws Exception {
        // Dummy-Pokemon
        PokemonSpecies species = new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null);
        Box box = new Box(BoxName.BOX1, Edition.ROT);

        OwnedPokemon mon1 = new OwnedPokemon(species, "Bulbi", 12, Edition.ROT, box);
        mon1.setId(1L);
        OwnedPokemon mon2 = new OwnedPokemon(species, "Pika", 9, Edition.BLAU, box);
        mon2.setId(2L);

        List<OwnedPokemon> pokemons = List.of(mon1, mon2);

        when(ownedService.getAllPokemon()).thenReturn(pokemons);

        mockMvc.perform(get("/api/pokemon"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Hier könntest du noch detaillierter prüfen, z.B. auf bestimmte Felder:
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testAddPokemon_success() throws Exception {
        // Input-DTO für den Request
        CreateOwnedDTO create = new CreateOwnedDTO();
        create.setPokedexId(25);
        create.setLevel(8);
        create.setEdition(Edition.ROT);
        create.setBox(BoxName.BOX1);
        create.setNickname("Testchu");

        // Dummy für Service/Antwort
        PokemonSpecies species = new PokemonSpecies(25, "Pikachu", PokemonType.ELEKTRO, null);
        Box box = new Box(BoxName.BOX1, Edition.ROT);
        OwnedPokemon dummy = new OwnedPokemon(species, "Testchu", 8, Edition.ROT, box);
        // simulate id after save
        dummy.setId(123L);

        OwnedPokemonDTO dto = OwnedPokemonDTO.from(dummy);

        when(ownedService.addPokemon(any(CreateOwnedDTO.class))).thenReturn(dummy);

        mockMvc.perform(post("/api/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("Testchu"))
                .andExpect(jsonPath("$.pokedexId").value(25));
    }

    @Test
    void testDeletePokemon_success() throws Exception {
        // Kein Rückgabewert, daher nur verify
        doNothing().when(ownedService).deletePokemonById(1L);

        mockMvc.perform(delete("/api/pokemon/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePokemon_notFound() throws Exception {
        // Werfe die gleiche Exception wie dein Service/Handler
        doThrow(new NotFoundException("Nicht gefunden")).when(ownedService).deletePokemonById(99L);

        mockMvc.perform(delete("/api/pokemon/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
