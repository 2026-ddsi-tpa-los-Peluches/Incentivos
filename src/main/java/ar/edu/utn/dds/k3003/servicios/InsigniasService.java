package ar.edu.utn.dds.k3003.servicios;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.mappers.InsigniaMapper;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import ar.edu.utn.dds.k3003.repositories.InsigniaDeDonadorRepository;
import ar.edu.utn.dds.k3003.repositories.InsigniaRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

// CRUD del catálogo de insignias y de la lista de insignias por donador (InsigniasDeDonador).
// No conoce a los otros módulos (Donaciones, Donadores y Entidades): validar que el donador
// exista y notificarles altas/bajas es responsabilidad de Fachada, que llama a este servicio.
@Service
public class InsigniasService {

  private final InsigniaRepository insigniaRepository;
  private final InsigniaDeDonadorRepository insigniaDeDonadorRepository;
  private final InsigniaMapper insigniaMapper = new InsigniaMapper();
  private final Counter insigniasCreadas;

  public InsigniasService(
      InsigniaRepository insigniaRepository,
      InsigniaDeDonadorRepository insigniaDeDonadorRepository,
      MeterRegistry meterRegistry) {
    this.insigniaRepository = insigniaRepository;
    this.insigniaDeDonadorRepository = insigniaDeDonadorRepository;
    this.insigniasCreadas =
        Counter.builder("incentivos.insignias.creadas")
            .description("Cantidad de insignias creadas")
            .register(meterRegistry);
  }

  // Los DTO manejan el id como String, pero las entidades lo tienen como Integer (autoincremental).
  // Trata un id null/no numérico como "no encontrado".
  public Optional<Insignia> buscarInsigniaModel(String id) {
    try {
      return id == null ? Optional.empty() : insigniaRepository.findById(Integer.valueOf(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public InsigniaDTO getInsignia(String id) {
    Insignia insignia =
        buscarInsigniaModel(id)
            .orElseThrow(
                () -> new NoSuchElementException("No se encontró la insignia con ID: " + id));
    return insigniaMapper.toDTO(insignia);
  }

  public List<InsigniaDTO> getAllInsignias() {
    return insigniaRepository.findAll().stream().map(insigniaMapper::toDTO).toList();
  }

  public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO) {
    if (insigniaDTO == null) {
      throw new IllegalArgumentException("Insignia null");
    }

    // Si viene un id explícito, validamos que no exista. Si viene null, lo genera la base.
    if (insigniaDTO.id() != null && buscarInsigniaModel(insigniaDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una insignia con el mismo ID");
    }

    Insignia insignia = insigniaMapper.toModel(insigniaDTO);
    Insignia guardada = insigniaRepository.save(insignia);

    insigniasCreadas.increment();

    return insigniaMapper.toDTO(guardada);
  }

  public List<InsigniaDTO> obtenerInsigniasPorIds(List<String> ids) {
    return ids.stream()
        .map(id -> buscarInsigniaModel(id).orElseThrow(NoSuchElementException::new))
        .map(insigniaMapper::toDTO)
        .toList();
  }

  public void agregarInsigniaADonador(String donadorID, String insigniaID) {
    buscarInsigniaModel(insigniaID)
        .orElseThrow(() -> new NoSuchElementException("No existe la insignia"));

    InsigniasDeDonador donador =
        insigniaDeDonadorRepository
            .findByDonadorId(donadorID)
            .orElseGet(() -> insigniaDeDonadorRepository.save(new InsigniasDeDonador(donadorID)));

    donador.agregarInsignia(insigniaID);

    insigniaDeDonadorRepository.save(donador);
  }

  public void quitarInsigniaDeDonador(String donadorID, String insigniaID) {
    buscarInsigniaModel(insigniaID)
        .orElseThrow(() -> new NoSuchElementException("No existe la insignia"));

    InsigniasDeDonador donador =
        insigniaDeDonadorRepository
            .findByDonadorId(donadorID)
            // No debería llegar a este caso nunca
            .orElseGet(() -> insigniaDeDonadorRepository.save(new InsigniasDeDonador(donadorID)));

    donador.quitarInsignia(insigniaID);

    insigniaDeDonadorRepository.save(donador);
  }
}
