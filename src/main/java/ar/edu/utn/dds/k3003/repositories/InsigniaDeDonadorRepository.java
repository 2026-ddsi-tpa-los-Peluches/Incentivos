package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import java.util.Optional;

public interface InsigniaDeDonadorRepository {
  public InsigniasDeDonador save(InsigniasDeDonador insigniasDeDonador);

  public Optional<InsigniasDeDonador> findByDonadorId(String donadorId);
}
