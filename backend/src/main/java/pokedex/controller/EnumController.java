package pokedex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokedex.model.BoxName;
import pokedex.model.Edition;

@RestController
@RequestMapping("/api")
public class EnumController {

    @GetMapping("/editions")
    public Edition[] getEditions() {
        return Edition.values();
    }

    @GetMapping("/boxnames")
    public BoxName[] getBoxNames() {
        return BoxName.values();
    }
}
