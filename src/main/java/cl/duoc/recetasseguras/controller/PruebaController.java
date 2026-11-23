
package cl.duoc.recetasseguras.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PruebaController {

    @GetMapping("/privado")
    public Map<String, String> privado() {
        return Map.of("mensaje", "Acceso autorizado con JWT");
    }
}
