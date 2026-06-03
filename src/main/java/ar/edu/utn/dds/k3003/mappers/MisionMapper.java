package ar.edu.utn.dds.k3003.mappers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.model.Mision;

public class MisionMapper {

  public Mision toModel(MisionDTO dto) {
    Mision mision = new Mision();
    mision.setId(dto.id());
    mision.setNombre(dto.nombre());
    mision.setInsigniaId(dto.insigniaID());
    mision.setCategoriaInicio(dto.categoriaInicio());
    mision.setCategoriaFin(dto.categoriaFin());
    mision.setTipoMision(dto.tipo());
    return mision;
  }

  public MisionDTO toDTO(Mision model) {
    return new MisionDTO(
        model.getId(),
        model.getNombre(),
        model.getInsigniaId(),
        model.getCategoriaInicio(),
        model.getCategoriaFin(),
        model.getTipoMision()
    );
  }
}