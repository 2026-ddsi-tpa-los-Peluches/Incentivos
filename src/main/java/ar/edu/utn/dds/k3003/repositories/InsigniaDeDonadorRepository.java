package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import java.util.Optional;

/**
 * Interfaz de repositorio de Insignias por Donador. La usa la Fachada como tipo de campo para
 * poder funcionar tanto con la implementación JPA (producción) como con la InMemory (tests).
 */
public interface InsigniaDeDonadorRepository {

  InsigniasDeDonador save(InsigniasDeDonador insigniasDeDonador);

  Optional<InsigniasDeDonador> findByDonadorId(String donadorId);
}
