package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.TipoMisionEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "misiones")
public class Mision {

  // El id numérico ascendente lo genera la base de datos (IDENTITY / autoincremental).
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Column(name = "insignia_id")
  private String insigniaId;

  @Enumerated(EnumType.STRING)
  @Column(name = "categoria_inicio")
  private CategoriaDonadorEnum categoriaInicio;

  @Enumerated(EnumType.STRING)
  @Column(name = "categoria_fin")
  private CategoriaDonadorEnum categoriaFin;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_mision")
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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