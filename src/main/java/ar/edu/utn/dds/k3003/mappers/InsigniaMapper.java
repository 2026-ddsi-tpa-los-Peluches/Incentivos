package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.model.Insignia;

public class InsigniaMapper {

  public Insignia toModel(InsigniaDTO dto) {
    Insignia insignia = new Insignia();
    insignia.setId(dto.id());
    insignia.setNombre(dto.nombre());
    insignia.setDescripcion(dto.descripcion());
    return insignia;
  }

  public InsigniaDTO toDTO(Insignia model) {
    return new InsigniaDTO(model.getId(), model.getNombre(), model.getDescripcion());
  }
}
