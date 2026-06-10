package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Insignia;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio de Insignias. La usa la Fachada como tipo de campo para poder
 * funcionar tanto con la implementación JPA (producción) como con la InMemory (tests).
 */
public interface InsigniaRepository {

  Optional<Insignia> findById(Integer id);

  Insignia save(Insignia insignia);

  List<Insignia> findAll();
}
