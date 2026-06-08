package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MisionDeDonadorRepository
    extends JpaRepository<MisionDeDonador, String> {

  // donadorId es la @Id (equivale a findById, lo mantenemos por compatibilidad).
  Optional<MisionDeDonador> findByDonadorId(String donadorId);

  // misionActualId es una columna normal: Spring Data genera la query sola.
  Optional<MisionDeDonador> findByMisionActualId(String misionActualId);
}