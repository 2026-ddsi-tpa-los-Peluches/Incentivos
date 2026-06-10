package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Mision;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMisionRepository implements MisionRepository {

  private final List<Mision> misiones = new ArrayList<>();
  private final AtomicInteger idSecuencial = new AtomicInteger(1);

  @Override
  public Optional<Mision> findById(Integer id) {
    return this.misiones.stream().filter(m -> m.getId().equals(id)).findFirst();
  }

  @Override
  public Mision save(Mision mision) {
    mision.setId(idSecuencial.getAndIncrement());
    this.misiones.add(mision);
    return mision;
  }

  @Override
  public List<Mision> findAll() {
    return new ArrayList<>(misiones);
  }
}
