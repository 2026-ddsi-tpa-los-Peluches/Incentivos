package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component

public class Cron {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Cron.class);

    private final Fachada fachada;
    private final RestTemplate restTemplate = new RestTemplate();

    private final boolean keepAliveEnabled;
    private final String externalUrl;

    public Cron(
            Fachada fachada,
            @Value("${KEEP_ALIVE_ENABLED:false}") boolean keepAliveEnabled,
            @Value("${RENDER_EXTERNAL_URL:}") String externalUrl) {
        this.fachada = fachada;
        this.keepAliveEnabled = keepAliveEnabled;
        this.externalUrl = externalUrl;
    }

    // Cada 2 minutos revisa las misiones de todos los donadores (la anterior y la actual).
    @Scheduled(fixedRate = 120000)
    public void revisarEstadoMisiones() {
        fachada.revisarEstadoMisiones();
    }

    // Keep-alive para Render free tier (duerme el servicio a los ~15 min de inactividad).
    // URL pública del servicio, así que no hay nada hardcodeado acá.
    @Scheduled(fixedRate = 720000)
    public void mantenerServicioDespierto() {
        if (!keepAliveEnabled || externalUrl == null || externalUrl.isBlank()) {
            return;
        }
        try {
            restTemplate.getForObject(externalUrl + "/ping", String.class);
        } catch (Exception e) {
            log.warn("Keep-alive: no se pudo hacer ping a {}: {}", externalUrl, e.getMessage());
        }
    }

}
