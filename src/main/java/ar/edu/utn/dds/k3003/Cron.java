package ar.edu.utn.dds.k3003;
import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component

public class Cron {
    
    private final Fachada fachada;

    public Cron(Fachada fachada) {
        this.fachada = fachada;
    }

    @Scheduled(fixedRate = 30000)
    public void mantenerServicios() {

    }
    // Cada minuto revisa las misiones de todos los donadores (la anterior y la actual).
    @Scheduled(fixedRate = 60000)
    public void revisarEstadoMisiones() {
        fachada.revisarEstadoMisiones();
    }

}
