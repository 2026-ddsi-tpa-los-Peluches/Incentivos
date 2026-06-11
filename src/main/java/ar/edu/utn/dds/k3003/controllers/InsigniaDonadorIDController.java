package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.dtos.InsigniaIDRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowire;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/insigniasDonador/{donadorID}")
public class InsigniaDonadorIDController {

    private final Fachada fachada;

    @Autowired
    public InsigniaDonadorIDController(Fachada fachada) {
        this.fachada = fachada;
    }

    // Opcion 1 utilizando @RequestMapping
    // asignar insignias a un DONADOR
    @RequestMapping(method = RequestMethod.POST)
    // MODIFICAR
    public ResponseEntity<Void> asignarInsignia(@PathVariable String donadorID,
            @RequestBody InsigniaIDRequest insigniaID) {
        // Obtengo el string posta, la insigniaID
        String insignia = insigniaID.getInsigniaID();
        // Busco el DTO y todo cae
        InsigniaDTO insigniaDTO = fachada.getInsignia(insignia);
        try {
            fachada.asignarInsigniaADonador(donadorID, insigniaDTO);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(412).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).build();
        }
    }

    // Opcion 2 utilizando @GetMapping
    @GetMapping
    public ResponseEntity<?> getAllInsigniasDonador(@PathVariable String donadorID) {
        List<InsigniaDTO> insignias = fachada.getInsigniasDeDonador(donadorID);
        if (insignias == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(insignias);

    }
}