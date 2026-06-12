package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.mappers.InsigniaMapper;
import ar.edu.utn.dds.k3003.mappers.MisionMapper;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.InsigniasDeDonador;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import ar.edu.utn.dds.k3003.repositories.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Fachada implements FachadaIncentivos {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Fachada.class);

  @Autowired
  private final InsigniaRepository insigniaRepository;
  @Autowired
  private final MisionRepository misionRepository;
  @Autowired
  private final InsigniaDeDonadorRepository InsigniasDeDonadorRepository;
  @Autowired
  private final MisionDeDonadorRepository misionDeDonadorRepository;
  private final InsigniaMapper insigniaMapper = new InsigniaMapper();
  private final MisionMapper misionMapper = new MisionMapper();

  // Métricas (Micrometer -> Datadog)
  private final MeterRegistry meterRegistry;
  private final Counter misionesCreadas;
  private final Counter insigniasCreadas;
  private final Counter misionesCompletadas;


  // Constructor usado por Spring: inyecta las implementaciones JPA (persistencia real)
  // y el MeterRegistry (que exporta a Datadog).
  @Autowired
  public Fachada(
      InsigniaRepository insigniaRepository,
      MisionRepository misionRepository,
      InsigniaDeDonadorRepository insigniaDeDonadorRepository,
      MisionDeDonadorRepository misionDeDonadorRepository,
      MeterRegistry meterRegistry) {
    this.insigniaRepository = insigniaRepository;
    this.misionRepository = misionRepository;
    this.InsigniasDeDonadorRepository = insigniaDeDonadorRepository;
    this.misionDeDonadorRepository = misionDeDonadorRepository;
    this.meterRegistry = meterRegistry;
    this.misionesCreadas = crearContadorMisionesCreadas();
    this.insigniasCreadas = crearContadorInsigniasCreadas();
    this.misionesCompletadas = crearContadorMisionesCompletadas();
  }

  private Counter crearContadorMisionesCreadas() {
    return Counter.builder("incentivos.misiones.creadas")
        .description("Cantidad de misiones creadas")
        .register(meterRegistry);
  }

  private Counter crearContadorInsigniasCreadas() {
    return Counter.builder("incentivos.insignias.creadas")
        .description("Cantidad de insignias creadas")
        .register(meterRegistry);
  }

  private Counter crearContadorMisionesCompletadas() {
    return Counter.builder("incentivos.misiones.completadas")
        .description("Cantidad de misiones completadas por donadores")
        .register(meterRegistry);
  }

  // Los DTO manejan el id como String, pero las entidades lo tienen como Integer (autoincremental).
  // Estos helpers convierten y tratan un id null/no numérico como "no encontrado".
  private Optional<Insignia> buscarInsignia(String id) {
    try {
      return id == null ? Optional.empty() : insigniaRepository.findById(Integer.valueOf(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Optional<Mision> buscarMision(String id) {
    try {
      return id == null ? Optional.empty() : misionRepository.findById(Integer.valueOf(id));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private FachadaDonaciones fachadaDonaciones;
  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

  // Cliente REST para notificar (push) a Donadores y Entidades cuando un donador
  // gana una insignia o se le asigna una misión. Se cablea en IntegracionFachadas.
  // Queda null en los tests (que instancian la Fachada por reflection): el push se omite.
  private ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient donadoresYEntidadesClient;


  public InsigniaDTO getInsignia(String id) {
    Insignia insignia = buscarInsignia(id).orElseThrow(() -> new NoSuchElementException("No se encontró la insignia con ID: " + id));
    return insigniaMapper.toDTO(insignia);
  }

  public MisionDTO getMision(String id) {
    Mision mision = buscarMision(id).orElseThrow(() -> new NoSuchElementException("No se encontró la misión con ID: " + id));
    return misionMapper.toDTO(mision);
  }

  public List<InsigniaDTO> getAllInsignias() {
    return insigniaRepository.findAll().stream()
        .map(insigniaMapper::toDTO)
        .toList();
  }

  public List<MisionDTO> getAllMisiones() {
    return misionRepository.findAll().stream()
        .map(misionMapper::toDTO)
        .toList();
  }

  @Override
  public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO) {

    if (insigniaDTO == null) {
      throw new IllegalArgumentException("Insignia null");
    }

    // Si viene un id explícito, validamos que no exista. Si viene null, lo genera la base.
    if (insigniaDTO.id() != null && buscarInsignia(insigniaDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una insignia con el mismo ID");
    }

    Insignia insignia = insigniaMapper.toModel(insigniaDTO);
    Insignia guardada = insigniaRepository.save(insignia);

    insigniasCreadas.increment();

    return insigniaMapper.toDTO(guardada);
  }

  @Override
  public MisionDTO agregarMision(MisionDTO misionDTO) {

    if (misionDTO == null) {
      throw new IllegalArgumentException("Mision nula");
    }

    // Si viene un id explícito, validamos que no exista. Si viene null, lo genera la base.
    if (misionDTO.id() != null && buscarMision(misionDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una misión con el mismo ID");
    }

    Mision mision = misionMapper.toModel(misionDTO);
    Mision guardada = misionRepository.save(mision);

    misionesCreadas.increment();

    return misionMapper.toDTO(guardada);
  }

  @Override
  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {
    // Los ids de insignias del donador los guarda DyE: los leemos de sus estadísticas y
    // enriquecemos cada id con el catálogo local de insignias.
    List<String> insigniasID;
    try {
      insigniasID = fachadaDonadoresYEntidades.estadisticasDonador(donadorID).insigniasID();
    } catch (NoSuchElementException e) {
      return null; // donador/estadísticas inexistentes -> el controller responde 404
    }

    if (insigniasID == null) {
      return List.of();
    }

    return insigniasID.stream()
        .map(id -> buscarInsignia(id).orElseThrow(NoSuchElementException::new))
        .map(insigniaMapper::toDTO)
        .toList();
  }
  @Override
  public MisionDTO getMisionEnCursoDeDonador(String donadorID) throws NoSuchElementException {
    // La misión actual del donador la guarda DyE (un único id): la leemos de sus estadísticas
    // y la enriquecemos con el catálogo local de misiones.
    DonadorStatsDTO stats = fachadaDonadoresYEntidades.estadisticasDonador(donadorID);

    String misionId = stats == null ? null : stats.misionActualID();

    if (misionId == null) {
      throw new NoSuchElementException("El donador no tiene una misión en curso");
    }

    Mision mision =
        buscarMision(misionId)
            .orElseThrow(
                () -> new NoSuchElementException("No se encontró la misión con ID: " + misionId));

    return misionMapper.toDTO(mision);
  }

  @Override
  public void asignarMisionADonador(String donadorID, MisionDTO misionDTO) {

    try {
      fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (misionDTO == null) {
      throw new IllegalArgumentException("Mision nula");
    }

    buscarMision(misionDTO.id())
        .orElseThrow(() -> new NoSuchElementException("No existe la misión"));

    MisionDeDonador misionDeDonador =
        misionDeDonadorRepository
            .findByDonadorId(donadorID)
            .orElseGet(() -> misionDeDonadorRepository.save(new MisionDeDonador(donadorID)));

    misionDeDonador.agregarMision(misionDTO.id());

    misionDeDonadorRepository.save(misionDeDonador);

    // Push (best-effort) a Donadores y Entidades: avisamos la misión asignada para sus
    // estadísticas. Si DyE está caído, la asignación local ya quedó persistida y no la revertimos.
    notificarMisionADonadoresYEntidades(donadorID, misionDTO.id());
  }

  @Override
  public void asignarInsigniaADonador(String donadorID, InsigniaDTO insigniaDTO) {
    try {
      fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (insigniaDTO == null) {
      throw new IllegalArgumentException("Insignia nula");
    }

    buscarInsignia(insigniaDTO.id())
        .orElseThrow(() -> new NoSuchElementException("No existe la insignia"));

    InsigniasDeDonador donador =
        InsigniasDeDonadorRepository.findByDonadorId(donadorID)
            .orElseGet(() -> InsigniasDeDonadorRepository.save(new InsigniasDeDonador(donadorID)));

    donador.agregarInsignia(insigniaDTO.id());

    InsigniasDeDonadorRepository.save(donador);

    // Push (best-effort) a Donadores y Entidades: avisamos la insignia ganada para sus
    // estadísticas. Si DyE está caído, la asignación local ya quedó persistida y no la revertimos.
    notificarInsigniaADonadoresYEntidades(donadorID, insigniaDTO.id());
  }

  private void notificarInsigniaADonadoresYEntidades(String donadorID, String insigniaID) {
    if (donadoresYEntidadesClient == null) {
      return;
    }
    try {
      donadoresYEntidadesClient.asignarInsigniaADonador(donadorID, insigniaID);
    } catch (RuntimeException e) {
      log.warn("No se pudo notificar la insignia {} del donador {} a Donadores y Entidades: {}",
          insigniaID, donadorID, e.getMessage());
    }
  }

  private void notificarMisionADonadoresYEntidades(String donadorID, String misionID) {
    if (donadoresYEntidadesClient == null) {
      return;
    }
    try {
      donadoresYEntidadesClient.asignarMisionADonador(donadorID, misionID);
    } catch (RuntimeException e) {
      log.warn("No se pudo notificar la misión {} del donador {} a Donadores y Entidades: {}",
          misionID, donadorID, e.getMessage());
    }
  }

  @Override
  public void procesarDonador(String donadorID) {
    try {
    fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    
    MisionDTO mision = getMisionEnCursoDeDonador(donadorID);

    if (fachadaDonaciones == null) {
      return;
    }

    var donador = fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    String categoriaActual = donador.categoria();

if (!categoriaActual.equals(mision.categoriaInicio().name())) {
    throw new IllegalStateException("El donador no cumple con la categoría inicial de la misión");
}



    List<DonacionDTO> donaciones =
        fachadaDonaciones.buscarPorDonadorYFechaInicio(donadorID, LocalDate.of(1900, 1, 1));

      
    Boolean cumplida = revisarEstadoMision(donadorID, mision, donaciones);

    if (cumplida) {

      misionesCompletadas.increment();

      Insignia insignia = buscarInsignia(mision.insigniaID()).orElseThrow();

      asignarInsigniaADonador(donadorID, insigniaMapper.toDTO(insignia));

      fachadaDonadoresYEntidades.modifcarCategoria(donadorID, mision.categoriaFin().name());

      misionDeDonadorRepository
          .findByDonadorId(donadorID)
          .ifPresent(
              misionDeDonador -> {
                misionDeDonador.setMisionActualId(null);
                misionDeDonadorRepository.save(misionDeDonador);
              });
    }
  }

  @Override
  public void setFachadaDonaciones(FachadaDonaciones fachadaDonaciones) {
    this.fachadaDonaciones = fachadaDonaciones;
  }

  @Override
  public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
    this.fachadaDonadoresYEntidades = fachadaDonadoresYEntidades;
  }

  public void setDonadoresYEntidadesClient(
      ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient donadoresYEntidadesClient) {
    this.donadoresYEntidadesClient = donadoresYEntidadesClient;
  }

  public Boolean revisarEstadoMision(
      String donadorID, MisionDTO mision, List<DonacionDTO> donaciones) {

    switch (mision.tipo()) {
      case COMPLETITUD:
        //La mision consiste en realizar donaciones a 3 categorias distintas de productos.
        List<String> categorias = new ArrayList<>();
        int distintasCategorias = 0;
        for (DonacionDTO donacion : donaciones) {
          if (!categorias.contains(donacion.productoID())) {
            categorias.add(donacion.productoID());
            distintasCategorias++;
          }
        }
        return distintasCategorias >= 3;
      case DONACIONES_EXITOSAS:
        //La misión consiste en realizar 20 donaciones exitosas (aceptadas).
        int contadorExitosas = 0;
        for (DonacionDTO donacion : donaciones) {
          if (donacion.estado() == EstadoDonacionEnum.ACEPTADA) {
            contadorExitosas++;
          }
        }
        return contadorExitosas >= 20;
      case DONACIONES_ASCENDENTES:
        //La misión consiste en realizar 5 donaciones con cantidades ascendentes (cada donación debe ser de una cantidad mayor a la anterior).  
    if (donaciones.size() < 5) {
        return false;
    }

    List<DonacionDTO> ultimas5 = donaciones
        .subList(donaciones.size() - 5, donaciones.size());

    int cantidadAnterior = 0;

    for (DonacionDTO donacion : ultimas5) {
        if (donacion.cantidad() < cantidadAnterior) {
            return false;
        }
        cantidadAnterior = donacion.cantidad();
    }

    return true;
      case REVOLUCION_DONADORA:
        int cantidadDonacionesMas50=0;
        for(DonacionDTO donacion : donaciones) {
          if (donacion.cantidad()>50) {
            cantidadDonacionesMas50++;
          }
        }
        return cantidadDonacionesMas50 > 10;
      default:
        return false;
    }
  }
}