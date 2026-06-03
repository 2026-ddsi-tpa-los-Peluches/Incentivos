package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Insignia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.mappers.InsigniaMapper;
import lombok.val;

public class InMemoryInsigniaRepository implements InsigniaRepository {

  private List<Insignia> insignias;
  private AtomicLong idSecuencial = new AtomicLong(1);
  private InsigniaMapper insigniaMapper = new InsigniaMapper();

  public InMemoryInsigniaRepository() {
    this.insignias = new ArrayList<>();
  }

  public Optional<Insignia> findById(String id) {
    return this.insignias.stream().filter(i -> i.getId().equals(id)).findFirst();
  }

  public Insignia save(Insignia insignia) {
    Insignia insigniaConId = insignia;
    insigniaConId.setId(String.valueOf(idSecuencial.getAndIncrement()));

    this.insignias.add(insigniaConId);
    return this.findById(insigniaConId.getId()).get();
  }

  public Insignia deleteById(String id) {
    val insignia = this.findById(id);
    this.insignias.remove(insignia.get());
    return insignia.get();
  }

  public List<InsigniaDTO> findAll() {
    List<Insignia> copia = new ArrayList<>(insignias);
    return copia.stream().map(insigniaMapper::toDTO).toList();
  }
}
