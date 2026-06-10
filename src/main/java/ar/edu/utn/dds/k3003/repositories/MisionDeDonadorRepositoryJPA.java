package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Implementación JPA del repositorio de Misiones por Donador (Spring Data genera el bean). */
@Repository
public interface MisionDeDonadorRepositoryJPA
    extends JpaRepository<MisionDeDonador, String>, MisionDeDonadorRepository {}
