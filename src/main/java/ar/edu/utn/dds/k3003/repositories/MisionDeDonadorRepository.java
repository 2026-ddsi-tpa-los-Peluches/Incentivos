package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import java.util.Optional;

public interface MisionDeDonadorRepository {
  public MisionDeDonador save(MisionDeDonador misionDeDonador);

  public Optional<MisionDeDonador> findByDonadorId(String donadorId);

  public Optional<MisionDeDonador> findByMisionActualId(String misionActualId);
}
