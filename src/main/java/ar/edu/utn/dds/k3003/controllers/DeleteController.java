package ar.edu.utn.dds.k3003.controllers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteController {

    @PersistenceContext
    private EntityManager entityManager;

    // Vacía todas las tablas del módulo y reinicia los contadores de id (IDENTITY),
    // así el próximo insert vuelve a arrancar en 1. Usamos TRUNCATE ... RESTART IDENTITY
    // (en vez de deleteAll(), que borra filas pero NO reinicia la secuencia).
    // CASCADE limpia además las tablas de colección que referencian por FK.
    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<String> resetDatabase() {
        entityManager.createNativeQuery(
                "TRUNCATE TABLE "
                        + "insignias, "
                        + "misiones, "
                        + "insignias_de_donador, "
                        + "insignias_de_donador_insignias, "
                        + "misiones_de_donador, "
                        + "misiones_de_donador_misiones "
                        + "RESTART IDENTITY CASCADE")
                .executeUpdate();
        return ResponseEntity.ok("Base de datos reseteada correctamente (ids reiniciados)");
    }
}
