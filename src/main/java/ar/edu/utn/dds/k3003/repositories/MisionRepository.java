package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Mision;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio de Misiones. La usa la Fachada como tipo de campo para poder
 * funcionar tanto con la implementación JPA (producción) como con la InMemory (tests).
 */
public interface MisionRepository {

  Optional<Mision> findById(Integer id);

  Mision save(Mision mision);

  List<Mision> findAll();
}
