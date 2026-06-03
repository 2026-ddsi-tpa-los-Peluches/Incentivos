package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/procesamiento/{donadorID}")
public class  ProcesamientoController {

    private Fachada fachada;

    public ProcesamientoController(Fachada fachada) {
        this.fachada = fachada;
    }

    // Opcion 1 utilizando @RequestMapping
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> procesar(@RequestBody String donadorID) {
        try {
            fachada.procesarDonador(donadorID);
            //retorno 204 si todo esta  bien
            return ResponseEntity.noContent().build();
        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(412).build();
        }
        catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
