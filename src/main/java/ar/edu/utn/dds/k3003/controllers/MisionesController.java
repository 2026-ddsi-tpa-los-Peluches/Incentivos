package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/misiones")
public class MisionesController {

    private Fachada fachada;

    @Autowired
    public MisionesController(Fachada fachada) {
        this.fachada = fachada;
    }

    // Opcion 1 utilizando @RequestMapping
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MisionDTO> addMision(@RequestBody MisionDTO mision) {
        MisionDTO misionAgregada = fachada.agregarMision(mision);
        return ResponseEntity.ok(misionAgregada);
    }

    // Opcion 2 utilizando @GetMapping
    @GetMapping
    public ResponseEntity<?> getMisiones() {
        List<MisionDTO> misiones = fachada.getAllMisiones();
            if (misiones == null) {
                return ResponseEntity.notFound().build();
            }
        return ResponseEntity.ok(misiones);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MisionDTO> getMision(@PathVariable String id) {
        try {
            MisionDTO mision = fachada.getMision(id);
            return ResponseEntity.ok(mision);
        }
        catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

}

