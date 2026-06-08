package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsigniaDeDonadorRepository
    extends JpaRepository<InsigniasDeDonador, String> {

  // donadorId es la @Id, así que esto equivale a findById,
  // pero lo dejamos con este nombre para no tocar la fachada.
  Optional<InsigniasDeDonador> findByDonadorId(String donadorId);
}