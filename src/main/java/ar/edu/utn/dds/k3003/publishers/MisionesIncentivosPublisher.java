package ar.edu.utn.dds.k3003.publishers;

import ar.edu.utn.dds.k3003.events.MisionCompletadaEvent;
import ar.edu.utn.dds.k3003.events.MisionProgresoActualizadoEvent;
import org.springframework.stereotype.Component;

@Component
public class MisionesIncentivosPublisher {

    private final EventPublisher eventPublisher;

    public MisionesIncentivosPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarProgreso(MisionProgresoActualizadoEvent evento) {
        eventPublisher.publicar("mision-progreso-exchange", evento);
    }

    public void notificarMisionCompletada(MisionCompletadaEvent evento) {
        eventPublisher.publicar("mision-completada-exchange", evento);
    }
}