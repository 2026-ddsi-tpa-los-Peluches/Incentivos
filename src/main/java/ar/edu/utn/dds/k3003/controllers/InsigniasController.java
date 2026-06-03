package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowire;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/insignias")
public class InsigniasController {

  private final Fachada fachada;

  @Autowired
  public InsigniasController(Fachada fachada) {
    this.fachada = fachada;
  }

  // Opcion 1 utilizando @RequestMapping
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<InsigniaDTO> addInsignia(@RequestBody InsigniaDTO insignia) {
    InsigniaDTO insigniaAgregada = fachada.agregarInsignia(insignia);
    return ResponseEntity.ok(insigniaAgregada);
  }

  // Opcion 2 utilizando @GetMapping
  @GetMapping
  public ResponseEntity<?> getAllInsignias() {
      List<InsigniaDTO> insignias = fachada.getAllInsignias();
      if (insignias == null ){
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(insignias);

  }
    @GetMapping("/{id}")
    public ResponseEntity<InsigniaDTO> getInsigniaID(@PathVariable String id) {
        try {
            InsigniaDTO insignia = fachada.getInsignia(id);
            return ResponseEntity.ok(insignia);
        }
        catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

}
