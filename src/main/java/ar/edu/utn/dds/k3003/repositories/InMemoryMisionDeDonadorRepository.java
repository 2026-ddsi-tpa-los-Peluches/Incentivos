package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMisionDeDonadorRepository implements MisionDeDonadorRepository {

  private Map<String, MisionDeDonador> data = new HashMap<>();

  public MisionDeDonador save(MisionDeDonador misionDeDonador) {
    data.put(misionDeDonador.getDonadorId(), misionDeDonador);
    return misionDeDonador;
  }

  public Optional<MisionDeDonador> findByDonadorId(String donadorId) {
    return Optional.ofNullable(data.get(donadorId));
  }

  public Optional<MisionDeDonador> findByMisionActualId(String misionActualId) {
    return data.values().stream()
        .filter(m -> misionActualId.equals(m.getMisionActualId()))
        .findFirst();
  }
}
