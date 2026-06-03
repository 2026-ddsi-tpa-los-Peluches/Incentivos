package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.mappers.MisionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import lombok.val;

public class InMemoryMisionRepository implements MisionRepository {

  private List<Mision> misiones;
  private AtomicLong idSecuencial = new AtomicLong(1);
  private MisionMapper misionMapper = new MisionMapper();

  public InMemoryMisionRepository() {
    this.misiones = new ArrayList<>();
  }

  public Optional<Mision> findById(String id) {
    return this.misiones.stream().filter(m -> m.getId().equals(id)).findFirst();
  }

  public Mision save(Mision mision) {
    Mision misionConId = mision;
    misionConId.setId(String.valueOf(idSecuencial.getAndIncrement()));

    this.misiones.add(misionConId);
    return this.findById(misionConId.getId()).get();
  }

  public Mision deleteById(String id) {
    val mision = this.findById(id);
    this.misiones.remove(mision.get());
    return mision.get();
  }

  public List<MisionDTO> findAll() {
    List<Mision> copias = new ArrayList<>();
    for (Mision mision : this.misiones) {
      copias.add(mision);
    }
    return copias.stream().map(misionMapper::toDTO).toList();
  }
}
