package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Insignia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Implementación JPA del repositorio de Insignias (Spring Data genera el bean). */
@Repository
public interface InsigniaRepositoryJPA
    extends JpaRepository<Insignia, Integer>, InsigniaRepository {}
