
package cl.duoc.recetasseguras.controller;

import cl.duoc.recetasseguras.model.Comentario;
import cl.duoc.recetasseguras.model.Receta;
import cl.duoc.recetasseguras.repository.ComentarioRepository;
import cl.duoc.recetasseguras.repository.RecetaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class RecetaController {

    private final RecetaRepository repository;
    private final ComentarioRepository comentarios;

    private static final Path CARPETA =
            Paths.get("uploads").toAbsolutePath().normalize();

    public RecetaController(
            RecetaRepository repository,
            ComentarioRepository comentarios) {
        this.repository = repository;
        this.comentarios = comentarios;
    }

    @GetMapping({"/", "/recetas"})
    public String inicio() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/recetas/buscar")
    public String buscar(
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String tipo,
            @RequestParam(defaultValue = "") String ingrediente,
            @RequestParam(defaultValue = "") String pais,
            @RequestParam(defaultValue = "") String dificultad,
            Model model) {

        List<Receta> resultados = repository.findAll().stream()
            .filter(r -> r.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .filter(r -> r.getTipo().toLowerCase().contains(tipo.toLowerCase()))
            .filter(r -> r.getIngrediente().toLowerCase().contains(ingrediente.toLowerCase()))
            .filter(r -> r.getPais().toLowerCase().contains(pais.toLowerCase()))
            .filter(r -> r.getDificultad().toLowerCase().contains(dificultad.toLowerCase()))
            .toList();

        model.addAttribute("recetas", resultados);
        return "buscar";
    }

    @GetMapping("/recetas/detalle/{id}")
    public String detalle(@PathVariable int id, Model model) {
        return repository.findById(id)
            .map(receta -> {
                model.addAttribute("receta", receta);
                model.addAttribute("comentarios",
                    comentarios.findByRecetaIdOrderByIdDesc(id));
                return "detalle";
            })
            .orElse("redirect:/recetas/buscar");
    }

    @PostMapping("/recetas/{id}/archivo")
    public String subirArchivo(
            @PathVariable int id,
            @RequestParam("archivo") MultipartFile archivo,
            Authentication auth) throws IOException {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        Receta receta = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Receta no encontrada"));

        if (archivo.isEmpty() ||
                archivo.getSize() > 20L * 1024 * 1024) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Archivo vacío o mayor a 20 MB");
        }

        Map<String, String> extensiones = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "video/mp4", ".mp4",
            "video/webm", ".webm"
        );

        String tipo = archivo.getContentType();

        if (tipo == null || !extensiones.containsKey(tipo)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Formato no permitido");
        }

        Files.createDirectories(CARPETA);

        String nombre = UUID.randomUUID() + extensiones.get(tipo);
        Path destino = CARPETA.resolve(nombre).normalize();

        if (!destino.startsWith(CARPETA)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        archivo.transferTo(destino);

        String url = "/uploads/" + nombre;

        if (tipo.startsWith("image/")) {
            receta.setImagen(url);
        } else {
            receta.setVideo(url);
        }

        repository.save(receta);

        return "redirect:/recetas/detalle/" + id;
    }

    @PostMapping("/recetas/{id}/comentar")
    public String comentar(
            @PathVariable int id,
            @RequestParam String texto,
            @RequestParam int valoracion,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        Receta receta = repository.findById(id).orElse(null);

        if (receta == null) {
            return "redirect:/recetas/buscar";
        }

        if (texto == null || texto.isBlank()
                || texto.length() > 1000
                || valoracion < 1 || valoracion > 5) {
            return "redirect:/recetas/detalle/" + id;
        }

        Comentario comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(auth.getName());
        comentario.setTexto(texto);
        comentario.setValoracion(valoracion);

        comentarios.save(comentario);

        return "redirect:/recetas/detalle/" + id;
    }
}
