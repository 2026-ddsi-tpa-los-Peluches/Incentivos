package ar.edu.utn.dds.k3003.publishers;



import ar.edu.utn.dds.k3003.events.insigniaEvents.InsigniaEliminadaEvent;
import ar.edu.utn.dds.k3003.events.insigniaEvents.InsigniaAgregadaEvent;
import ar.edu.utn.dds.k3003.model.Insignia;
import org.springframework.stereotype.Component;

@Component
public class InsigniasPublisher {

    private final EventPublisher eventPublisher;

    public InsigniasPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarInsigniaCreada(Insignia insignia) {
        InsigniaAgregadaEvent evento = new InsigniaAgregadaEvent(
                insignia.getId(),
                insignia.getNombre(),
                insignia.getDescripcion()
        );
        eventPublisher.publicar("insignia-agregada", evento);
        System.out.println("Insignia creada enviada a RabbitMQ con ID: " + insignia.getId());
    }

    public void notificarInsigniaEliminada(String insigniaId) {
        InsigniaEliminadaEvent evento = new InsigniaEliminadaEvent(insigniaId);
        eventPublisher.publicar("insignia-eliminada", evento);
        System.out.println("Insignia eliminada enviada a RabbitMQ con ID: " + insigniaId);
    }
}