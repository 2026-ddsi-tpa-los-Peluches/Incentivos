package ar.edu.utn.dds.k3003.events.insigniaEvents;

import lombok.Getter;

import java.time.Instant;


@Getter
public class InsigniaEliminadaEvent {
    private String insigniaId;
    private Instant fechaEvento;

    public InsigniaEliminadaEvent(String insigniaId) {
        this.insigniaId = insigniaId;
        this.fechaEvento = Instant.now();
    }

}