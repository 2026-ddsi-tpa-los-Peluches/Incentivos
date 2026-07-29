package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorStatsDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.InsigniaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import ar.edu.utn.dds.k3003.componentes.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.mappers.InsigniaMapper;
import ar.edu.utn.dds.k3003.model.Insignia;
import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import ar.edu.utn.dds.k3003.servicios.InsigniasService;
import ar.edu.utn.dds.k3003.servicios.MisionesService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Orquestador de Incentivos. El CRUD de insignias y misiones vive en InsigniasService/MisionesService.
@Service
public class Fachada implements FachadaIncentivos {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Fachada.class);

  private final InsigniasService insigniasService;
  private final MisionesService misionesService;
  private final InsigniaMapper insigniaMapper = new InsigniaMapper();

  // Métrica propia de Fachada: las de creación de insignias/misiones viven en cada servicio.
  private final Counter misionesCompletadas;

  private FachadaDonaciones fachadaDonaciones;
  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;

  // Cliente REST para notificar a DyE. Queda null en los tests (se omite el push).
  private DonadoresYEntidadesClient donadoresYEntidadesClient;

  @Autowired
  public Fachada(
      InsigniasService insigniasService, MisionesService misionesService, MeterRegistry meterRegistry) {
    this.insigniasService = insigniasService;
    this.misionesService = misionesService;
    this.misionesCompletadas =
        Counter.builder("incentivos.misiones.completadas")
            .description("Cantidad de misiones completadas por donadores")
            .register(meterRegistry);
  }

  public InsigniaDTO getInsignia(String id) {
    return insigniasService.getInsignia(id);
  }

  public MisionDTO getMision(String id) {
    return misionesService.getMision(id);
  }

  public List<InsigniaDTO> getAllInsignias() {
    return insigniasService.getAllInsignias();
  }

  public List<MisionDTO> getAllMisiones() {
    return misionesService.getAllMisiones();
  }

  @Override
  public InsigniaDTO agregarInsignia(InsigniaDTO insigniaDTO) {
    return insigniasService.agregarInsignia(insigniaDTO);
  }

  @Override
  public MisionDTO agregarMision(MisionDTO misionDTO) {
    return misionesService.agregarMision(misionDTO);
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

    return insigniasService.obtenerInsigniasPorIds(insigniasID);
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

    return misionesService.getMision(misionId);
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

    misionesService
        .buscarMisionModel(misionDTO.id())
        .orElseThrow(() -> new NoSuchElementException("No existe la misión"));

    misionesService.agregarMisionADonador(donadorID, misionDTO.id());

    // Avisamos la misión asignada a DyE (best-effort).
    notificarMisionADonadoresYEntidades(donadorID, misionDTO.id());
  }

  // Cancela la misión en curso del donador (sin tocar insignia ni categoría).
  public void quitarMisionDeDonador(String donadorID) {
    try {
      fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    MisionDeDonador misionDeDonador =
        misionesService
            .buscarPorDonador(donadorID)
            .orElseThrow(() -> new NoSuchElementException("El donador no tiene una misión en curso"));

    if (misionDeDonador.getMisionActualId() == null) {
      throw new NoSuchElementException("El donador no tiene una misión en curso");
    }

    misionesService.limpiarMisionActual(donadorID);

    notificarMisionADonadoresYEntidades(donadorID, null);
  }

  // Categoría actual del donador. La usa el controller para validar la asignación de una misión.
  public String categoriaActualDeDonador(String donadorID) {
    return fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID).categoria();
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

    insigniasService.agregarInsigniaADonador(donadorID, insigniaDTO.id());

    // Avisamos la insignia ganada a DyE (best-effort).
    notificarInsigniaADonadoresYEntidades(donadorID, insigniaDTO.id());
  }

  public void quitarInsigniaDonador(String donadorID, InsigniaDTO insigniaDTO) {
    try {
      fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (insigniaDTO == null) {
      throw new IllegalArgumentException("Insignia nula");
    }

    insigniasService.quitarInsigniaDeDonador(donadorID, insigniaDTO.id());

    // Avisamos la baja de la insignia a DyE (best-effort).
    notificarQuitarInsigniaADonadoresYEntidades(donadorID, insigniaDTO.id());
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

  private void notificarQuitarInsigniaADonadoresYEntidades(String donadorID, String insigniaID) {
    if (donadoresYEntidadesClient == null) {
      return;
    }
    try {
      donadoresYEntidadesClient.quitarInsigniaADonador(donadorID, insigniaID);
    } catch (RuntimeException e) {
      log.warn("No se pudo notificar la baja de la insignia {} del donador {} a Donadores y Entidades: {}",
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

    // equalsIgnoreCase porque DyE devuelve la categoría con otra capitalización que el enum.
    if (!mision.categoriaInicio().name().equalsIgnoreCase(categoriaActual)) {
      throw new IllegalStateException("El donador no cumple con la categoría inicial de la misión");
    }

    List<DonacionDTO> donaciones;
    try {
      donaciones =
          fachadaDonaciones.buscarPorDonadorYFechaInicio(donadorID, LocalDate.of(1900, 1, 1));
    } catch (RuntimeException e) {
      // Si Donaciones no responde, salimos sin romper el procesamiento.
      log.warn("No se pudo obtener el historial de donaciones del donador {}: {}",
          donadorID, e.getMessage());
      return;
    }

    Boolean cumplida = revisarEstadoMision(donadorID, mision, donaciones);

    if (cumplida) {

      misionesCompletadas.increment();

      Insignia insignia = insigniasService.buscarInsigniaModel(mision.insigniaID()).orElseThrow();

      asignarInsigniaADonador(donadorID, insigniaMapper.toDTO(insignia));

      fachadaDonadoresYEntidades.modifcarCategoria(donadorID, mision.categoriaFin().name());

      misionesService.limpiarMisionActual(donadorID);

      // Avisamos a DyE que el donador ya no tiene misión en curso.
      notificarMisionADonadoresYEntidades(donadorID, null);
    }
  }

  // Regresión: revisa la misión que subió al donador a su categoría actual y, si ya no se cumple,
  // le saca la insignia y lo baja de categoría.
  public void revisarMisionAnterior(String donadorID) {
    try {
      fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (fachadaDonaciones == null) {
      return;
    }

    var donador = fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
    Optional<MisionDTO> misionCompletada =
        misionesService.buscarMisionPorCategoriaActual(donador.categoria());
    if (misionCompletada.isEmpty()) {
      return; // categoría base o sin misión asociada -> nada que regresar
    }
    MisionDTO mision = misionCompletada.get();

    List<DonacionDTO> donaciones;
    try {
      donaciones =
          fachadaDonaciones.buscarPorDonadorYFechaInicio(donadorID, LocalDate.of(1900, 1, 1));
    } catch (RuntimeException e) {
      // Si Donaciones no responde, salimos sin romper el procesamiento.
      log.warn("No se pudo obtener el historial de donaciones del donador {}: {}",
          donadorID, e.getMessage());
      return;
    }

    Boolean cumplida = revisarEstadoMision(donadorID, mision, donaciones);

    if (!cumplida) {

      Insignia insignia = insigniasService.buscarInsigniaModel(mision.insigniaID()).orElseThrow();

      quitarInsigniaDonador(donadorID, insigniaMapper.toDTO(insignia));

      fachadaDonadoresYEntidades.modifcarCategoria(donadorID, mision.categoriaInicio().name());

      misionesService.limpiarMisionActual(donadorID);

      // Avisamos a DyE que el donador ya no tiene misión en curso.
      notificarMisionADonadoresYEntidades(donadorID, null);
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

  public void setDonadoresYEntidadesClient(DonadoresYEntidadesClient donadoresYEntidadesClient) {
    this.donadoresYEntidadesClient = donadoresYEntidadesClient;
  }

  // Categoría de un producto (contra Donaciones). Null si no se puede resolver.
  private String obtenerCategoriaDeProducto(String productoID) {
    if (productoID == null || fachadaDonaciones == null) {
      return null;
    }
    try {
      var producto = fachadaDonaciones.buscarProductoPorID(productoID);
      return producto == null ? null : producto.categoriaID();
    } catch (RuntimeException e) {
      log.warn("No se pudo resolver la categoría del producto {}: {}", productoID, e.getMessage());
      return null;
    }
  }

  public Boolean revisarEstadoMision(
      String donadorID, MisionDTO mision, List<DonacionDTO> donaciones) {

    switch (mision.tipo()) {
      case COMPLETITUD:
        // Donaciones a 3 categorías distintas. El DTO solo trae productoID, así que resolvemos
        // la categoría de cada producto (distinto) contra Donaciones.
        Set<String> productosDistintos = new HashSet<>();
        for (DonacionDTO donacion : donaciones) {
          if (donacion.productoID() != null) {
            productosDistintos.add(donacion.productoID());
          }
        }
        Set<String> categoriasDistintas = new HashSet<>();
        for (String productoID : productosDistintos) {
          String categoriaID = obtenerCategoriaDeProducto(productoID);
          if (categoriaID != null) {
            categoriasDistintas.add(categoriaID);
          }
        }
        return categoriasDistintas.size() >= 3;
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
        // Las últimas 5 donaciones deben tener cantidades estrictamente ascendentes.
        // Respetamos el orden en que las entrega Donaciones (es su responsabilidad mandarlas
        // ordenadas por fecha); acá solo las recibimos y evaluamos.
        List<Integer> cantidades = new ArrayList<>();
        for (DonacionDTO donacion : donaciones) {
          if (donacion.cantidad() != null) {
            cantidades.add(donacion.cantidad());
          }
        }
        if (cantidades.size() < 5) {
          return false;
        }
        // Tomamos las últimas 5 (en el orden recibido) y verificamos que sean ascendentes.
        List<Integer> ultimas5 = cantidades.subList(cantidades.size() - 5, cantidades.size());
        for (int i = 1; i < ultimas5.size(); i++) {
          if (ultimas5.get(i) <= ultimas5.get(i - 1)) {
            return false;
          }
        }
        return true;
      case REVOLUCION_DONADORA:
        int cantidadDonacionesMas50 = 0;
        for (DonacionDTO donacion : donaciones) {
          if (donacion.cantidad() != null && donacion.cantidad() > 50) {
            cantidadDonacionesMas50++;
          }
        }
        return cantidadDonacionesMas50 > 10;
      default:
        return false;
    }
  }

  // Lo llama el Cron para cada donador.
  public void revisarEstadoMisiones() {
    List<MisionDeDonador> misionesDeDonadores = misionesService.findAll();
    for (MisionDeDonador misionDeDonador : misionesDeDonadores) {
      String donadorID = misionDeDonador.getDonadorId();
      try {
        revisarMisionAnterior(donadorID); // chequea la misión anterior
        procesarDonador(donadorID);       // chequea la misión actual
      } catch (Exception e) {
        log.warn("Error al procesar el donador {}: {}", donadorID, e.getMessage());
      }
    }
  }
}
