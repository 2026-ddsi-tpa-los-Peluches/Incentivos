package ar.edu.utn.dds.k3003.componentes;

import ar.edu.utn.dds.k3003.dtos.InsigniaIDRequest;
import ar.edu.utn.dds.k3003.dtos.MisionIDRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DonadoresYEntidadesClient {

    // JdkClientHttpRequestFactory (java.net.http) en vez del default, porque el
    // SimpleClientHttpRequestFactory de RestTemplate NO soporta PATCH (que usa el push de misión).
    private final RestTemplate restTemplate =
            new RestTemplate(new org.springframework.http.client.JdkClientHttpRequestFactory());
    private final String baseUrl;
    private final boolean isMock;

    // Toma la URL del servicio de Donadores y Entidades desde la config (env var DONADORES_Y_ENTIDADES).
    // Si no está seteada, cae al default localhost y trabaja en modo "mock" (no hace llamadas reales).
    public DonadoresYEntidadesClient(
            @Value("${DONADORES_Y_ENTIDADES:http://localhost:8082}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.isMock = "http://localhost:8082".equals(baseUrl);
    }

    // POST /insigniasDonador/{donadorID}  -> agrega una insignia a la LISTA del donador en DyE.
    // Body: { "insigniaID": "<id>" }
    public void asignarInsigniaADonador(String donadorId, String insigniaId) {
        if (isMock) return;

        try {
            String url = baseUrl + "/insigniasDonador/" + donadorId;
            restTemplate.postForObject(url, new InsigniaIDRequest(insigniaId), Void.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al asignar la insignia " + insigniaId + " al donador " + donadorId
                            + " en Donadores y Entidades", e);
        }
    }

    // PATCH /donadores/{donadorID}/misionActual  -> setea (reemplaza) el id de misión actual
    // del donador en DyE. Body: { "misionID": "<id>" }
    public void asignarMisionADonador(String donadorId, String misionId) {
        if (isMock) return;

        try {
            String url = baseUrl + "/donadores/" + donadorId + "/misionActual";
            restTemplate.patchForObject(url, new MisionIDRequest(misionId), Void.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al asignar la misión " + misionId + " al donador " + donadorId
                            + " en Donadores y Entidades", e);
        }
    }
}
