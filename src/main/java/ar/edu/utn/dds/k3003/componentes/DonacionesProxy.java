package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Adapter REST contra el servicio de Donaciones.
 * Incentivos solo necesita {@link #buscarPorDonadorYFechaInicio} (para procesar misiones).
 * El resto de métodos de la interfaz lanza UnsupportedOperationException.
 *
 * Si la URL no está configurada (env var DONACIONES_SERVICE_URL vacía) trabaja en modo "mock":
 * devuelve lista vacía en vez de romper, para que procesarDonador no falle.
 */
@Service
public class DonacionesProxy implements FachadaDonaciones {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl;
    private final boolean isMock;

    public DonacionesProxy(@Value("${DONACIONES_SERVICE_URL:}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.isMock = (baseUrl == null || baseUrl.isBlank());
    }

    // GET /donaciones/search?donadorID={id}&fechaInicio={fecha} -> donaciones del donador desde esa fecha.
    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha)
            throws NoSuchElementException {
        if (isMock) return List.of();

        try {
            String url = baseUrl + "/donaciones/search?donadorID=" + donadorID + "&fechaInicio=" + fecha;
            DonacionDTO[] donaciones = restTemplate.getForObject(url, DonacionDTO[].class);
            return donaciones == null ? List.of() : Arrays.asList(donaciones);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al consultar donaciones del donador " + donadorID + " en Donaciones", e);
        }
    }

    // --- Métodos de la interfaz que Incentivos NO usa ---

    @Override
    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado)
            throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    // GET /productos/{id} -> trae el producto (incluye categoriaID), usado para evaluar
    // la misión de COMPLETITUD (contar categorías distintas de las donaciones).
    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {
        if (isMock) {
            throw new NoSuchElementException(
                    "Producto " + productoID + " no disponible (Donaciones en modo mock)");
        }
        try {
            String url = baseUrl + "/productos/" + productoID;
            ProductoDTO producto = restTemplate.getForObject(url, ProductoDTO.class);
            if (producto == null) {
                throw new NoSuchElementException("No existe el producto " + productoID);
            }
            return producto;
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al consultar el producto " + productoID + " en Donaciones", e);
        }
    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID)
            throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
        // No-op
    }

    @Override
    public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
        // No-op
    }
}
