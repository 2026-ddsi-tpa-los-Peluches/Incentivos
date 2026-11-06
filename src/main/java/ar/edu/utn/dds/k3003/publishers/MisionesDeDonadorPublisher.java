package ar.edu.utn.dds.k3003.publishers;

import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.MisionDTO;
import ar.edu.utn.dds.k3003.dtos.MisionHistoricaDTO;
import ar.edu.utn.dds.k3003.events.donadorEvents.AsignarMisionDonadorEvent;
import ar.edu.utn.dds.k3003.events.donadorEvents.MisionDonadorCompletadaEvent;
import ar.edu.utn.dds.k3003.events.donadorEvents.MisionDonadorLimpiadaEvent;
import ar.edu.utn.dds.k3003.events.donadorEvents.MisionDonadorProgresoActualizadoEvent;
import ar.edu.utn.dds.k3003.model.Mision;
import ar.edu.utn.dds.k3003.model.MisionDeDonador;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MisionesDeDonadorPublisher {

    private final EventPublisher eventPublisher;

    public MisionesDeDonadorPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void notificarAsignarMision(String donadorId, Mision mision, int objetivo) {
        AsignarMisionDonadorEvent evento = new AsignarMisionDonadorEvent(
                donadorId,
               mision.getId().toString(),
                mision.getNombre(),
                mision.getInsigniaId(),
                mision.getTipoMision().toString(),
                0, // Progreso inicial al asignar
                objetivo,
                mision.getCategoriaInicio().toString(),
                mision.getCategoriaFin().toString()
        );

        eventPublisher.publicar("donador-mision-asignar", evento);
        System.out.println("Asignación de misión enviada a RabbitMQ para el donador ID: " + donadorId);
    }



    public void notificarProgreso(MisionDeDonador misionDeDonador, Integer nuevoProgreso, MisionDTO mision, Integer objetivo) {
        MisionDonadorProgresoActualizadoEvent eventoProgreso = new MisionDonadorProgresoActualizadoEvent(
                misionDeDonador.getDonadorId(),
                mision.id(),
                nuevoProgreso,
                Instant.now()

        );
        eventPublisher.publicar("mision-donador-progreso", eventoProgreso);

        System.out.println("Progreso de misión enviado a RabbitMQ para el donador ID: " + misionDeDonador.getDonadorId());

    }



    public void notificarMisionCompletada(String donadorId, String misionId, String nombreMision) {

        // 1. Armamos el DTO del historial con el nombre y el timestamp actual de finalización
        MisionHistoricaDTO historialDTO = new MisionHistoricaDTO(
                misionId,
                nombreMision,
                Instant.now()
        );

        // 2. Creamos el evento de misión completada encapsulando los datos
        MisionDonadorCompletadaEvent eventoCompletada = new MisionDonadorCompletadaEvent(
                donadorId,
                historialDTO
        );

        // 3. Publicamos el evento al exchange correspondiente en RabbitMQ
        eventPublisher.publicar("mision-donador-completada", eventoCompletada);

        System.out.println("Misión completada enviada a RabbitMQ para el donador ID: " + donadorId);
    }

    public void notificarMisionLimpiada(String donadorId) {

        MisionDonadorLimpiadaEvent evento = new MisionDonadorLimpiadaEvent(donadorId);

        eventPublisher.publicar("mision-donador-limpiada", evento);
        System.out.println("Misión limpiada enviada a RabbitMQ para donador: " + donadorId);
    }
}