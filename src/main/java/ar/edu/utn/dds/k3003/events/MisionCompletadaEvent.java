package ar.edu.utn.dds.k3003.events;

import ar.edu.utn.dds.k3003.dtos.MisionHistoricaDTO;

import java.time.LocalDateTime;

public class MisionCompletadaEvent {
    private String donadorId;
    private MisionHistoricaDTO misionCompletada; // La que pasa al historial
    private String nuevaInsigniaId;
    private String nuevaInsigniaNombre;
    private String nuevaCategoria;
    private LocalDateTime fechaEvento;

    // Constructores, Getters y Setters
    public MisionCompletadaEvent(String donadorId, MisionHistoricaDTO misionCompletada, String nuevaInsigniaId, String nuevaInsigniaNombre, String nuevaCategoria, LocalDateTime fechaEvento) {
        this.donadorId = donadorId;
        this.misionCompletada = misionCompletada;
        this.nuevaInsigniaId = nuevaInsigniaId;
        this.nuevaInsigniaNombre = nuevaInsigniaNombre;
        this.nuevaCategoria = nuevaCategoria;
        this.fechaEvento = fechaEvento;
    }

    public String getDonadorId() { return donadorId; }
    public MisionHistoricaDTO getMisionCompletada() { return misionCompletada; }
    public String getNuevaInsigniaId() { return nuevaInsigniaId; }
    public String getNuevaInsigniaNombre() { return nuevaInsigniaNombre; }
    public String getNuevaCategoria() { return nuevaCategoria; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
}