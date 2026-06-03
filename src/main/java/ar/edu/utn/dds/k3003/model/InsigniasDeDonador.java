package ar.edu.utn.dds.k3003.model;

import java.util.ArrayList;
import java.util.List;

public class InsigniasDeDonador {

  private String donadorId;
  private List<String> insigniasIds = new ArrayList<>();

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
