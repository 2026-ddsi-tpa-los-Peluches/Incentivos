package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Insignia;
import java.util.List;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import java.util.Optional;

public interface InsigniaRepository {
  public Optional<Insignia> findById(String id);

  public Insignia save(Insignia insignia);

  public Insignia deleteById(String id);

  public List<InsigniaDTO> findAll();
}
