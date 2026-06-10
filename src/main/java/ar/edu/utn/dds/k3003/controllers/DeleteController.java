package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteController {

    @Autowired
    private InsigniaDeDonadorRepositoryJPA insigniaDeDonadorRepositoryJPA;
    @Autowired
    private InsigniaRepositoryJPA insigniaRepositoryJPA;
    @Autowired
    private MisionRepositoryJPA misionRepositoryJPA;
    @Autowired
    private MisionDeDonadorRepositoryJPA misionDeDonadorRepositoryJPA;

    @PostMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        insigniaDeDonadorRepositoryJPA.deleteAll();
        insigniaRepositoryJPA.deleteAll();
        misionDeDonadorRepositoryJPA.deleteAll();
        misionRepositoryJPA.deleteAll();
        return ResponseEntity.ok("Base de datos reseteada correctamente");
    }
}