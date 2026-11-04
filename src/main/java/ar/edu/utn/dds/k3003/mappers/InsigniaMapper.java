package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.model.Insignia;

public class InsigniaMapper {

  public Insignia toModel(InsigniaDTO dto) {
    Insignia insignia = new Insignia();
    // El id lo genera la base (IDENTITY). No se copia el del DTO: si viene seteado,
    // save() hace merge en vez de insert y Hibernate 6.6 tira StaleObjectStateException.
    insignia.setNombre(dto.nombre());
    insignia.setDescripcion(dto.descripcion());
    return insignia;
  }

  public InsigniaDTO toDTO(Insignia model) {
    String id = model.getId() == null ? null : String.valueOf(model.getId());
    return new InsigniaDTO(id, model.getNombre(), model.getDescripcion());
  }
}
