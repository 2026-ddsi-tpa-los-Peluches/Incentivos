package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Implementación JPA del repositorio de Insignias por Donador (Spring Data genera el bean). */
@Repository
public interface InsigniaDeDonadorRepositoryJPA
    extends JpaRepository<InsigniasDeDonador, String>, InsigniaDeDonadorRepository {}
