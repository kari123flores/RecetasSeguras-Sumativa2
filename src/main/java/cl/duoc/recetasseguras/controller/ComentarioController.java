
package cl.duoc.recetasseguras.controller;

import cl.duoc.recetasseguras.model.Comentario;
import cl.duoc.recetasseguras.model.Receta;
import cl.duoc.recetasseguras.repository.ComentarioRepository;
import cl.duoc.recetasseguras.repository.RecetaRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recetas")
public class ComentarioController {

    private final ComentarioRepository comentarios;
    private final RecetaRepository recetas;

    public ComentarioController(
            ComentarioRepository comentarios,
            RecetaRepository recetas) {
        this.comentarios = comentarios;
        this.recetas = recetas;
    }

    public record ComentarioRequest(
        @NotBlank @Size(max = 1000) String texto,
        @Min(1) @Max(5) int valoracion
    ) {}

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<?> agregar(
            @PathVariable Integer id,
            @Valid @RequestBody ComentarioRequest request,
            Authentication auth) {

        Receta receta = recetas.findById(id).orElse(null);

        if (receta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Receta no encontrada"));
        }

        Comentario comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(auth.getName());
        comentario.setTexto(request.texto());
        comentario.setValoracion(request.valoracion());

        comentarios.save(comentario);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Comentario publicado",
                "usuario", comentario.getUsuario(),
                "valoracion", comentario.getValoracion()
            ));
    }

    @GetMapping("/{id}/comentarios")
    public List<Comentario> listar(@PathVariable Integer id) {
        return comentarios.findByRecetaIdOrderByIdDesc(id);
    }
}
