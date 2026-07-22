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

    @Scheduled(fixedRate= 5000) // En milisegundos, cada 5 segundos
    public void revisarEstadoMisiones() {
        fachada.revisarEstadoMisiones();
    }

}
