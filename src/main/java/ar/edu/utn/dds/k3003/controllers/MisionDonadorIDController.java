package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.dtos.MisionIDRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowire;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/misionesDonador/{donadorID}")
public class MisionDonadorIDController {

    private final Fachada fachada;

    @Autowired
    public MisionDonadorIDController(Fachada fachada) {
        this.fachada = fachada;
    }

    // Opcion 1 utilizando @RequestMapping
    //asignar mision a un DONADOR
    @RequestMapping(method = RequestMethod.POST)

    //MODIFICAR
        public ResponseEntity<Void> asignarMision(@PathVariable String donadorID,@RequestBody MisionIDRequest misionID) {
        // Aplico lo mismo que en Insignia
        String mision = misionID.getMisionID();
        MisionDTO misionDTO = fachada.getMision(mision);
        try {
            fachada.asignarMisionADonador(donadorID, misionDTO);
            return ResponseEntity.noContent().build(); // devuelve 204 al ser un void no es necesario que devuelva nada de parametros
        } catch (IllegalStateException e) {

            return ResponseEntity.status(404).build();
        } catch (NoSuchElementException e) {

            return ResponseEntity.status(412).build();

        }
    }

    // Mision en curso de DONADOR
    // Opcion 2 utilizando @GetMapping
    @GetMapping
    public ResponseEntity<?> getMisionEnCurso(@PathVariable String donadorID) {
        try {
            MisionDTO mision = fachada.getMisionEnCursoDeDonador(donadorID);
            if (mision == null) {
                return ResponseEntity.status(404).build();
            }
            return ResponseEntity.ok(mision);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }
}