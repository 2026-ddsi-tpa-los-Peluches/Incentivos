package ar.edu.utn.dds.k3003.publishers;

import ar.edu.utn.dds.k3003.events.donadorEvents.InsigniaDonadorAgregadaEvent;
import ar.edu.utn.dds.k3003.events.donadorEvents.InsigniaDonadorQuitadaEvent;
import org.springframework.stereotype.Component;

@Component
public class InsigniasDeDonadorPublisher {

    private final EventPublisher eventPublisher;

    public InsigniasDeDonadorPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarInsigniaAgregada(String donadorId, String insigniaId) {
        InsigniaDonadorAgregadaEvent evento = new InsigniaDonadorAgregadaEvent(donadorId, insigniaId);
        eventPublisher.publicar("insignia-donador-agregada", evento);
        System.out.println("Insignia " + insigniaId + " agregada enviada para donador: " + donadorId);
    }

    public void notificarInsigniaQuitada(String donadorId, String insigniaId) {
        InsigniaDonadorQuitadaEvent evento = new InsigniaDonadorQuitadaEvent(donadorId, insigniaId);
        eventPublisher.publicar("insignia-donador-quitada", evento);
        System.out.println("Insignia " + insigniaId + " quitada enviada para donador: " + donadorId);
    }
}