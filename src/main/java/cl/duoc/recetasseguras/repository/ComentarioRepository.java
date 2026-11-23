
package cl.duoc.recetasseguras.repository;

import cl.duoc.recetasseguras.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository
        extends JpaRepository<Comentario, Long> {

    List<Comentario> findByRecetaIdOrderByIdDesc(Integer recetaId);
}
