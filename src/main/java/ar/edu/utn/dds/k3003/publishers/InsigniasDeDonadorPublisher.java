package ar.edu.utn.dds.k3003.publishers;

import ar.edu.utn.dds.k3003.events.donadorEvents.DonadorInsigniaAgregadaEvent;
import ar.edu.utn.dds.k3003.events.donadorEvents.DonadorInsigniaQuitadaEvent;
import org.springframework.stereotype.Component;

@Component
public class InsigniasDeDonadorPublisher {

    private final EventPublisher eventPublisher;

    public InsigniasDeDonadorPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarInsigniaAgregada(String donadorId, String insigniaId, String nombre, String descripcion) {
        DonadorInsigniaAgregadaEvent evento = new DonadorInsigniaAgregadaEvent(donadorId, insigniaId, nombre, descripcion);
        eventPublisher.publicar("insignia-donador-agregada", evento);
        System.out.println("Insignia " + insigniaId + " agregada enviada para donador: " + donadorId);
    }

    public void notificarInsigniaQuitada(String donadorId, String insigniaId) {
        DonadorInsigniaQuitadaEvent evento = new DonadorInsigniaQuitadaEvent(donadorId, insigniaId);
        eventPublisher.publicar("insignia-donador-quitada", evento);
        System.out.println("Insignia " + insigniaId + " quitada enviada para donador: " + donadorId);
    }
}