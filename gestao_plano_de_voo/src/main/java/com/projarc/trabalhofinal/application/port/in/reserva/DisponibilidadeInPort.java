package com.projarc.trabalhofinal.application.port.in.reserva;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadeInPort {
    /** Vazio quando a aerovia não existe. */
    Optional<List<Integer>> getAltitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim);
    /** Vazio quando o aeroporto não existe. */
    Optional<List<Integer>> getVagasDisponiveis(String codigoAeroporto, LocalDateTime inicio, LocalDateTime fim);
}
