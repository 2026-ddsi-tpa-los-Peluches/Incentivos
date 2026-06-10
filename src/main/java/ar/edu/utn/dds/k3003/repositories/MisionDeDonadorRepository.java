package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import java.util.Optional;

/**
 * Interfaz de repositorio de Misiones por Donador. La usa la Fachada como tipo de campo para
 * poder funcionar tanto con la implementación JPA (producción) como con la InMemory (tests).
 */
public interface MisionDeDonadorRepository {

  MisionDeDonador save(MisionDeDonador misionDeDonador);

  Optional<MisionDeDonador> findByDonadorId(String donadorId);

  Optional<MisionDeDonador> findByMisionActualId(String misionActualId);
}
