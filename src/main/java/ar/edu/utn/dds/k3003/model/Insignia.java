package ar.edu.utn.dds.k3003.model;

public class Insignia {

  private String id;
  private String nombre;
  private String descripcion;

  // Constructor vacío
  public Insignia() {}

  // Constructor completo
  public Insignia(String nombre, String descripcion) {
    this.nombre = nombre;
    this.descripcion = descripcion;
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

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
