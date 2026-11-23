
package cl.duoc.recetasseguras.repository;

import cl.duoc.recetasseguras.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecetaRepository extends JpaRepository<Receta, Integer> {
}
