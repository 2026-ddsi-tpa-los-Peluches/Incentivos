package ar.edu.utn.dds.k3003.events.donadorEvents;

import ar.edu.utn.dds.k3003.dtos.MisionHistoricaDTO;
import lombok.Getter;

import java.time.Instant;

@Getter
public class MisionDonadorCompletadaEvent {
    private String donadorId;
    private MisionHistoricaDTO misionCompletada;
    private Instant fechaEvento;

    public MisionDonadorCompletadaEvent(String donadorId, MisionHistoricaDTO misionCompletada) {
        this.donadorId = donadorId;
        this.misionCompletada = misionCompletada;
        this.fechaEvento = Instant.now();
    }

}