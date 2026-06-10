package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Insignia;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryInsigniaRepository implements InsigniaRepository {

  private final List<Insignia> insignias = new ArrayList<>();
  private final AtomicInteger idSecuencial = new AtomicInteger(1);

  @Override
  public Optional<Insignia> findById(Integer id) {
    return this.insignias.stream().filter(i -> i.getId().equals(id)).findFirst();
  }

  @Override
  public Insignia save(Insignia insignia) {
    insignia.setId(idSecuencial.getAndIncrement());
    this.insignias.add(insignia);
    return insignia;
  }

  @Override
  public List<Insignia> findAll() {
    return new ArrayList<>(insignias);
  }
}
