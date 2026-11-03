package ar.edu.utn.dds.k3003.events;

import java.time.LocalDateTime;

public record MisionProgresoActualizadoEvent(
    String donadorId,
    String misionId,
    String nombreMision,
    String insigniaId,
    String tipoMision,
    Integer progreso,
    Integer objetivo,
    String categoriaInicio,
    String categoriaFin,
    LocalDateTime fechaEvento
) {}