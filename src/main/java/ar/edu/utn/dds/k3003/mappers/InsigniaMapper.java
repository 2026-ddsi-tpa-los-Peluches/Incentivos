package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.model.Insignia;

public class InsigniaMapper {

  public Insignia toModel(InsigniaDTO dto) {
    Insignia insignia = new Insignia();
    if (dto.id() != null && !dto.id().isBlank()) {
      insignia.setId(Integer.valueOf(dto.id()));
    }
    insignia.setNombre(dto.nombre());
    insignia.setDescripcion(dto.descripcion());
    return insignia;
  }

  public InsigniaDTO toDTO(Insignia model) {
    String id = model.getId() == null ? null : String.valueOf(model.getId());
    return new InsigniaDTO(id, model.getNombre(), model.getDescripcion());
  }
}
