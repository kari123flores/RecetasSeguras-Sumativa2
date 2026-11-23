
package cl.duoc.recetasseguras.controller;

import cl.duoc.recetasseguras.model.Receta;
import cl.duoc.recetasseguras.repository.RecetaRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recetas")
public class ArchivoController {

    private final RecetaRepository repository;

    private static final Path CARPETA =
        Paths.get("uploads").toAbsolutePath().normalize();

    public ArchivoController(RecetaRepository repository) {
        this.repository = repository;
    }

    @PostMapping(value = "/{id}/archivo",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> subir(
            @PathVariable Integer id,
            @RequestParam("archivo") MultipartFile archivo)
            throws IOException {

        Receta receta = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Receta no encontrada"));

        if (archivo.isEmpty() ||
            archivo.getSize() > 20 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Archivo vacío o mayor a 20 MB"));
        }

        String tipo = archivo.getContentType();

        Map<String, String> extensiones = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "video/mp4", ".mp4",
            "video/webm", ".webm"
        );

        if (tipo == null || !extensiones.containsKey(tipo)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Formato no permitido"));
        }

        Files.createDirectories(CARPETA);

        String nombre = UUID.randomUUID() + extensiones.get(tipo);
        Path destino = CARPETA.resolve(nombre);

        Files.copy(archivo.getInputStream(), destino);

        String url = "/uploads/" + nombre;

        if (tipo.startsWith("image/")) {
            receta.setImagen(url);
        } else {
            receta.setVideo(url);
        }

        repository.save(receta);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Archivo subido correctamente",
            "url", url
        ));
    }
}
