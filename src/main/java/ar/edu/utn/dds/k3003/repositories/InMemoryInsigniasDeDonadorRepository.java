package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryInsigniasDeDonadorRepository implements InsigniaDeDonadorRepository {

  private Map<String, InsigniasDeDonador> data = new HashMap<>();

  public InsigniasDeDonador save(InsigniasDeDonador insigniasDeDonador) {
    data.put(insigniasDeDonador.getDonadorId(), insigniasDeDonador);
    return insigniasDeDonador;
  }

  public Optional<InsigniasDeDonador> findByDonadorId(String donadorId) {
    return Optional.ofNullable(data.get(donadorId));
  }
}
