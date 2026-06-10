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
@Table(name = "misiones_de_donador")
public class MisionDeDonador {

  @Id
  @Column(name = "donador_id")
  private String donadorId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "misiones_de_donador_misiones",
      joinColumns = @JoinColumn(name = "donador_id"))
  @Column(name = "mision_id")
  private List<String> misionesIds = new ArrayList<>();

  @Column(name = "mision_actual_id")
  private String misionActualId;

  // Constructor vacío requerido por JPA
  protected MisionDeDonador() {}

  public MisionDeDonador(String donadorId) {
    this.donadorId = donadorId;
  }

  public String getDonadorId() {
    return donadorId;
  }

  public String getMisionActualId() {
    return misionActualId;
  }

  public void setMisionActualId(String misionActualId) {
    this.misionActualId = misionActualId;
  }

  public void agregarMision(String misionId) {
    if (!misionesIds.contains(misionId)) {
      misionesIds.add(misionId);
    }
    this.misionActualId = misionId;
  }
}
