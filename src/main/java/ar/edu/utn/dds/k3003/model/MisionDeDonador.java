package ar.edu.utn.dds.k3003.model;

import java.util.ArrayList;
import java.util.List;

public class MisionDeDonador {

  private String donadorId;
  private List<String> misionesIds = new ArrayList<>();
  private String misionActualId;

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
