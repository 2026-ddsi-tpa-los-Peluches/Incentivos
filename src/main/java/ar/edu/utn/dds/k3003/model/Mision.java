package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;

public class Mision {

  private String id;
  private String nombre;
  private String insigniaId;
  private CategoriaDonadorEnum categoriaInicio;
  private CategoriaDonadorEnum categoriaFin;
  private TipoMisionEnum tipoMision; 

  // Constructor vacío
  public Mision() {}

  // Constructor completo
  public Mision(
      String nombre,
      String insigniaId,
      CategoriaDonadorEnum categoriaInicio,
      CategoriaDonadorEnum categoriaFin,
      TipoMisionEnum tipoMision) {

    this.nombre = nombre;
    this.insigniaId = insigniaId;
    this.categoriaInicio = categoriaInicio;
    this.categoriaFin = categoriaFin;
    this.tipoMision = tipoMision;
  }

  // Getters y Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getInsigniaId() {
    return insigniaId;
  }

  public void setInsigniaId(String insigniaId) {
    this.insigniaId = insigniaId;
  }

  public CategoriaDonadorEnum getCategoriaInicio() {
    return categoriaInicio;
  }

  public void setCategoriaInicio(CategoriaDonadorEnum categoriaInicio) {
    this.categoriaInicio = categoriaInicio;
  }

  public CategoriaDonadorEnum getCategoriaFin() {
    return categoriaFin;
  }

  public void setCategoriaFin(CategoriaDonadorEnum categoriaFin) {
    this.categoriaFin = categoriaFin;
  }


  public TipoMisionEnum getTipoMision() {
    return tipoMision;
  }


  public void setTipoMision(TipoMisionEnum tipoMision) {
    this.tipoMision = tipoMision;
  }
}