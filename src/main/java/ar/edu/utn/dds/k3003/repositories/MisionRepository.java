package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Mision;
import java.util.Optional;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import java.util.List;


public interface MisionRepository {
  public Optional<Mision> findById(String id);

  public Mision save(Mision mision);

  public Mision deleteById(String id);

  public List<MisionDTO> findAll();
}
