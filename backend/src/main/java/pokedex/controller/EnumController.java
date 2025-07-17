package pokedex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokedex.model.BoxName;
import pokedex.model.Edition;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EnumController {

    // --- Boxnames ---
    @GetMapping("/boxnames")
    public BoxName[] getBoxNames() {
        return BoxName.values();
    }

    @GetMapping("/boxnames/mapping")
    public Map<String, String> getBoxNameMapping() {
        return Arrays.stream(BoxName.values())
                .collect(Collectors.toMap(
                        BoxName::getDisplayName,
                        Enum::name
                ));
    }

    // --- Editions ---
    @GetMapping("/editions")
    public Edition[] getEditions() {
        return Edition.values();
    }

    @GetMapping("/editions/mapping")
    public Map<String, String> getEditionMapping() {
        return Arrays.stream(Edition.values())
                .collect(Collectors.toMap(
                        Edition::getDisplayName,
                        Enum::name
                ));
    }
}