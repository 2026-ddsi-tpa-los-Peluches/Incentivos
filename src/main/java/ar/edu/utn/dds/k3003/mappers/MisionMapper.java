package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.model.Mision;

public class MisionMapper {

  public Mision toModel(MisionDTO dto) {
    Mision mision = new Mision();
    if (dto.id() != null && !dto.id().isBlank()) {
      mision.setId(Integer.valueOf(dto.id()));
    }
    mision.setNombre(dto.nombre());
    mision.setInsigniaId(dto.insigniaID());
    mision.setCategoriaInicio(dto.categoriaInicio());
    mision.setCategoriaFin(dto.categoriaFin());
    mision.setTipoMision(dto.tipo());
    return mision;
  }

  public MisionDTO toDTO(Mision model) {
    String id = model.getId() == null ? null : String.valueOf(model.getId());
    return new MisionDTO(
        id,
        model.getNombre(),
        model.getInsigniaId(),
        model.getCategoriaInicio(),
        model.getCategoriaFin(),
        model.getTipoMision()
    );
  }
}