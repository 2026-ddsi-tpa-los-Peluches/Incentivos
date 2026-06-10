package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Mision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Implementación JPA del repositorio de Misiones (Spring Data genera el bean). */
@Repository
public interface MisionRepositoryJPA
    extends JpaRepository<Mision, Integer>, MisionRepository {}
