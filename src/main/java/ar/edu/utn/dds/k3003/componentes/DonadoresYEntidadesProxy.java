package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.*;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaIncentivos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Adapter REST contra el servicio de Donadores y Entidades.
 * Implementa la interfaz {@link FachadaDonadoresYEntidades}, pero solo los métodos
 * que Incentivos realmente consume (buscarDonadorPorID y modifcarCategoria).
 * El resto lanza UnsupportedOperationException porque este módulo no los usa.
 */
@Service
public class DonadoresYEntidadesProxy implements FachadaDonadoresYEntidades {

    // JdkClientHttpRequestFactory (java.net.http) en vez del default, porque el
    // SimpleClientHttpRequestFactory de RestTemplate NO soporta PATCH (que usa modifcarCategoria).
    private final RestTemplate restTemplate =
            new RestTemplate(new org.springframework.http.client.JdkClientHttpRequestFactory());
    private final String baseUrl;

    public DonadoresYEntidadesProxy(@Value("${DONADORES_Y_ENTIDADES:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // GET /donadores/{id}  -> trae el donador. Si no existe, lanza NoSuchElementException.
    @Override
    public DonadorDTO buscarDonadorPorID(String donadorID) throws NoSuchElementException {
        try {
            String url = baseUrl + "/donadores/" + donadorID;
            DonadorDTO donador = restTemplate.getForObject(url, DonadorDTO.class);
            if (donador == null) {
                throw new NoSuchElementException("No existe el donador " + donadorID);
            }
            return donador;
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("No existe el donador " + donadorID);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al consultar el donador " + donadorID + " en Donadores y Entidades", e);
        }
    }

    // PATCH /donadores/{id}/categoria  body { "categoria": "<X>" } -> actualiza la categoría del donador.
    // DyE rechaza PUT en esta ruta ("Request method 'PUT' is not supported"); usa PATCH como en misionActual.
    @Override
    public DonadorDTO modifcarCategoria(String donadorID, String categoria)
            throws NoSuchElementException {
        try {
            String url = baseUrl + "/donadores/" + donadorID + "/categoria";
            HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("categoria", categoria));
            ResponseEntity<DonadorDTO> resp =
                    restTemplate.exchange(url, HttpMethod.PATCH, request, DonadorDTO.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("No existe el donador " + donadorID);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al modificar la categoría del donador " + donadorID
                            + " en Donadores y Entidades", e);
        }
    }

    // --- Métodos de la interfaz que Incentivos NO usa ---

    @Override
    public DonadorDTO agregarDonador(DonadorDTO donadorDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public EntidadBeneficaDTO agregarEntidad(EntidadBeneficaDTO entidadBeneficaDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public EntidadBeneficaDTO buscarEntidadPorID(String entidadID) throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public NecesidadMaterialDTO registrarNecesidad(NecesidadMaterialDTO necesidadMaterialDTO) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public QuejaDTO agregarQueja(QuejaDTO quejaDTO) throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public Boolean puedeDonar(String donadorID) throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public List<QuejaDTO> obtenerQuejasDe(String donadorID) throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public DonadorDTO modificarEstado(String donadorID, EstadoDonadorEnum estado)
            throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public List<NecesidadMaterialDTO> obtenerNecesidadesInsatisfechasDe(String productoSolicitadoID) {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    @Override
    public NecesidadMaterialDTO satisfacerNecesidad(String necesidadID, Integer cantidad)
            throws NoSuchElementException {
        throw new UnsupportedOperationException("No implementado en Incentivos");
    }

    // GET /donadores/{id}/estadisticas -> stats del donador en DyE.
    // El DonadorStatsDTO incluye la lista insigniasID[] y el misionActualID, que Incentivos
    // usa para responder sus GET /insigniasDonador/{id} y GET /misionesDonador/{id}.
    @Override
    public DonadorStatsDTO estadisticasDonador(String donadorID) {
        try {
            String url = baseUrl + "/donadores/" + donadorID + "/estadisticas";
            DonadorStatsDTO stats = restTemplate.getForObject(url, DonadorStatsDTO.class);
            if (stats == null) {
                throw new NoSuchElementException("No hay estadísticas para el donador " + donadorID);
            }
            return stats;
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("No existe el donador " + donadorID);
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al consultar estadísticas del donador " + donadorID
                            + " en Donadores y Entidades", e);
        }
    }

    @Override
    public void setFachadaIncentivos(FachadaIncentivos fachadaIncentivos) {
        // No-op: este proxy no necesita la referencia inversa.
    }
}
