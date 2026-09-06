package ar.edu.utn.dds.k3003.servicios;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.mappers.MisionMapper;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import ar.edu.utn.dds.k3003.repositories.MisionDeDonadorRepository;
import ar.edu.utn.dds.k3003.repositories.MisionRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

// CRUD del catálogo de misiones y de la relación misión-donador (MisionDeDonador).
// No conoce a los otros módulos: eso es responsabilidad de Fachada, que llama a este servicio.
@Service
public class MisionesService {

  private final MisionRepository misionRepository;
  private final MisionDeDonadorRepository misionDeDonadorRepository;
  private final MisionMapper misionMapper = new MisionMapper();
  private final Counter misionesCreadas;

  public MisionesService(
      MisionRepository misionRepository,
      MisionDeDonadorRepository misionDeDonadorRepository,
      MeterRegistry meterRegistry) {
    this.misionRepository = misionRepository;
    this.misionDeDonadorRepository = misionDeDonadorRepository;
    this.misionesCreadas =
        Counter.builder("incentivos.misiones.creadas")
            .description("Cantidad de misiones creadas")
            .register(meterRegistry);
  }

  // Los DTO manejan el id como String, pero las entidades lo tienen como Integer (autoincremental).
  // Trata un id null/no numérico como "no encontrado".
  public Optional<Mision> buscarMisionModel(String id) {
    try {
      return id == null ? Optional.empty() : misionRepository.findById(Integer.valueOf(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  public MisionDTO getMision(String id) {
    Mision mision =
        buscarMisionModel(id)
            .orElseThrow(
                () -> new NoSuchElementException("No se encontró la misión con ID: " + id));
    return misionMapper.toDTO(mision);
  }

  public List<MisionDTO> getAllMisiones() {
    return misionRepository.findAll().stream().map(misionMapper::toDTO).toList();
  }

  public MisionDTO agregarMision(MisionDTO misionDTO) {
    if (misionDTO == null) {
      throw new IllegalArgumentException("Mision nula");
    }

    // Si viene un id explícito, validamos que no exista. Si viene null, lo genera la base.
    if (misionDTO.id() != null && buscarMisionModel(misionDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una misión con el mismo ID");
    }

    Mision mision = misionMapper.toModel(misionDTO);
    Mision guardada = misionRepository.save(mision);

    misionesCreadas.increment();

    return misionMapper.toDTO(guardada);
  }

  // Borra una misión del catálogo. No valida si está referenciada como misión en curso o
  // histórica de algún donador (esas relaciones son ids sueltos, sin FK) — queda dangling.
  public void eliminarMision(String id) {
    buscarMisionModel(id)
        .orElseThrow(() -> new NoSuchElementException("No se encontró la misión con ID: " + id));
    misionRepository.deleteById(Integer.valueOf(id));
  }

  // La usa revisarMisionAnterior para encontrar qué misión fue la que subió al donador
  // a su categoría actual.
  public Optional<MisionDTO> buscarMisionPorCategoriaActual(String categoriaActual) {
    return misionRepository.findAll().stream()
        .map(misionMapper::toDTO)
        .filter(
            m -> m.categoriaFin() != null && m.categoriaFin().name().equalsIgnoreCase(categoriaActual))
        .findFirst();
  }

  public void agregarMisionADonador(String donadorID, String misionID) {
    MisionDeDonador misionDeDonador =
        misionDeDonadorRepository
            .findByDonadorId(donadorID)
            .orElseGet(() -> misionDeDonadorRepository.save(new MisionDeDonador(donadorID)));

    misionDeDonador.agregarMision(misionID);

    misionDeDonadorRepository.save(misionDeDonador);
  }

  // Pone la misión en curso en null sin tocar insignias ni categoría (esa decisión es de
  // Fachada, según si la misión se completó, se perdió o se canceló).
  public void limpiarMisionActual(String donadorID) {
    misionDeDonadorRepository
        .findByDonadorId(donadorID)
        .ifPresent(
            misionDeDonador -> {
              misionDeDonador.setMisionActualId(null);
              misionDeDonadorRepository.save(misionDeDonador);
            });
  }

  public Optional<MisionDeDonador> buscarPorDonador(String donadorID) {
    return misionDeDonadorRepository.findByDonadorId(donadorID);
  }

  public List<MisionDeDonador> findAll() {
    return misionDeDonadorRepository.findAll();
  }
}
