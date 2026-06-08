package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

@Service
public class Fachada implements FachadaIncentivos {

  private final InsigniaRepository insigniaRepository;
  private final MisionRepository misionRepository;
  private final InsigniaDeDonadorRepository InsigniasDeDonadorRepository;
  private final MisionDeDonadorRepository misionDeDonadorRepository;
  private final InsigniaMapper insigniaMapper = new InsigniaMapper();
  private final MisionMapper misionMapper = new MisionMapper();


  public Fachada(
      InsigniaRepository insigniaRepository,
      MisionRepository misionRepository,
      InsigniaDeDonadorRepository insigniaDeDonadorRepository,
      MisionDeDonadorRepository misionDeDonadorRepository) {
    this.insigniaRepository = insigniaRepository;
    this.misionRepository = misionRepository;
    this.InsigniasDeDonadorRepository = insigniaDeDonadorRepository;
    this.misionDeDonadorRepository = misionDeDonadorRepository;
  }

  private FachadaDonaciones fachadaDonaciones;
  private FachadaDonadoresYEntidades fachadaDonadoresYEntidades;


  public InsigniaDTO getInsignia(String id) {
    Insignia insignia =insigniaRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la insignia con ID: " + id));
    return insigniaMapper.toDTO(insignia);
  }

  public MisionDTO getMision(String id) {
    Mision mision = misionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la misión con ID: " + id));
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

    if (insigniaDTO.id() == null) {
      insigniaDTO =
          new InsigniaDTO(
              java.util.UUID.randomUUID().toString(),
              insigniaDTO.nombre(),
              insigniaDTO.descripcion());
    }

    if (insigniaRepository.findById(insigniaDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una insignia con el mismo ID");
    }

    Insignia insignia = insigniaMapper.toModel(insigniaDTO);
    Insignia guardada = insigniaRepository.save(insignia);

    return insigniaMapper.toDTO(guardada);
  }

  @Override
  public MisionDTO agregarMision(MisionDTO misionDTO) {

    if (misionDTO == null) {
      throw new IllegalArgumentException("Mision nula");
    }

    if (misionDTO.id() == null) {
      misionDTO =
          new MisionDTO(
              java.util.UUID.randomUUID().toString(),
              misionDTO.nombre(),
              misionDTO.insigniaID(),
              misionDTO.categoriaInicio(),
              misionDTO.categoriaFin(),
              misionDTO.tipo());
    }

    if (misionRepository.findById(misionDTO.id()).isPresent()) {
      throw new IllegalArgumentException("Ya existe una misión con el mismo ID");
    }

    Mision mision = misionMapper.toModel(misionDTO);
    Mision guardada = misionRepository.save(mision);

    return misionMapper.toDTO(guardada);
  }

  @Override
  public List<InsigniaDTO> getInsigniasDeDonador(String donadorID) {

    var existente = InsigniasDeDonadorRepository.findByDonadorId(donadorID);

    if (existente.isEmpty()) {
      try {
        var donador = fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);

        if (donador == null) {
          throw new RuntimeException("Donador inexistente");
        }

      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      InsigniasDeDonador nuevo = new InsigniasDeDonador(donadorID);
      InsigniasDeDonadorRepository.save(nuevo);
      existente = java.util.Optional.of(nuevo);
    }

    return existente.get().getInsigniasIds().stream()
            .map(id -> insigniaRepository.findById(id)
                    .orElseThrow(NoSuchElementException::new))
            .map(insigniaMapper::toDTO)
            .toList();
  }
  @Override
  public MisionDTO getMisionEnCursoDeDonador(String donadorID) throws NoSuchElementException {

    if (misionDeDonadorRepository.findByDonadorId(donadorID).isEmpty()) {
      try {
        fachadaDonadoresYEntidades.buscarDonadorPorID(donadorID);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    MisionDeDonador misionDeDonador =
        misionDeDonadorRepository
            .findByDonadorId(donadorID)
            .orElseGet(() -> misionDeDonadorRepository.save(new MisionDeDonador(donadorID)));

    String misionId = misionDeDonador.getMisionActualId();

    if (misionId == null) {
      throw new NoSuchElementException("El donador no tiene una misión en curso");
    }

    Mision mision =
        misionRepository
            .findById(misionId)
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

    misionRepository
        .findById(misionDTO.id())
        .orElseThrow(() -> new NoSuchElementException("No existe la misión"));

    MisionDeDonador misionDeDonador =
        misionDeDonadorRepository
            .findByDonadorId(donadorID)
            .orElseGet(() -> misionDeDonadorRepository.save(new MisionDeDonador(donadorID)));

    misionDeDonador.agregarMision(misionDTO.id());

    misionDeDonadorRepository.save(misionDeDonador);
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

    insigniaRepository
        .findById(insigniaDTO.id())
        .orElseThrow(() -> new NoSuchElementException("No existe la insignia"));

    InsigniasDeDonador donador =
        InsigniasDeDonadorRepository.findByDonadorId(donadorID)
            .orElseGet(() -> InsigniasDeDonadorRepository.save(new InsigniasDeDonador(donadorID)));

    donador.agregarInsignia(insigniaDTO.id());

    InsigniasDeDonadorRepository.save(donador);
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

      Insignia insignia = insigniaRepository.findById(mision.insigniaID()).orElseThrow();

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