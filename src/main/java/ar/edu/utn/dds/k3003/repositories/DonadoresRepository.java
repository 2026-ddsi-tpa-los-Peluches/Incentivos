package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Donador;
import java.util.Optional;

public interface DonadoresRepository extends JpaRepository<Donador, String> {
  Optional<Donador> findById(String id);

  Donador save(Donador donador);

  Donador deleteById(String id);
}
