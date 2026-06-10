package ar.edu.utn.dds.k3003.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "insignias_de_donador")
public class InsigniasDeDonador {

  @Id
  @Column(name = "donador_id")
  private String donadorId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "insignias_de_donador_insignias",
      joinColumns = @JoinColumn(name = "donador_id"))
  @Column(name = "insignia_id")
  private List<String> insigniasIds = new ArrayList<>();

  // Constructor vacío requerido por JPA
  protected InsigniasDeDonador() {}

  public InsigniasDeDonador(String donadorId) {
    this.donadorId = donadorId;
  }

  public String getDonadorId() {
    return donadorId;
  }

  public List<String> getInsigniasIds() {
    return insigniasIds;
  }

  public void agregarInsignia(String insigniaId) {
    if (!insigniasIds.contains(insigniaId)) {
      insigniasIds.add(insigniaId);
    }
  }
}
