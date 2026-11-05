package ar.edu.utn.dds.k3003.publishers;
import ar.edu.utn.dds.k3003.events.MisionEliminadaEvent;
import ar.edu.utn.dds.k3003.events.MisionAgregadaEvent;
import ar.edu.utn.dds.k3003.model.Mision;
import org.springframework.stereotype.Component;

@Component
public class MisionesPublisher {

    private final EventPublisher eventPublisher;

    public MisionesPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarMisionCreada(Mision mision) {
        MisionAgregadaEvent evento = new MisionAgregadaEvent(
                mision.getId(),
                mision.getNombre(),
                mision.getInsigniaId(),
                mision.getCategoriaInicio().toString(),
                mision.getCategoriaFin().toString(),
                mision.getTipoMision().toString()
        );
        eventPublisher.publicar("mision-agregada", evento);
        System.out.println(" Misión creada enviada a RabbitMQ con ID: " + mision.getId());
    }

    public void notificarMisionEliminada(String misionId) {
        MisionEliminadaEvent evento = new MisionEliminadaEvent(misionId);
        eventPublisher.publicar("mision-eliminada", evento);
        System.out.println(" Misión eliminada enviada a RabbitMQ con ID: " + misionId);
    }
}