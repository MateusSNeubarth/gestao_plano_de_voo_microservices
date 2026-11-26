package com.projarc.trabalhofinal.application.usecase.reserva;

import com.projarc.trabalhofinal.application.port.in.reserva.DisponibilidadeInPort;
import com.projarc.trabalhofinal.application.port.out.malha.RedeAereaPort;
import com.projarc.trabalhofinal.application.port.out.reserva.ReservaPort;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Altitudes;
import com.projarc.trabalhofinal.domain.model.reserva.ReservaAerovia;
import com.projarc.trabalhofinal.domain.model.reserva.ReservaEstacionamento;
import com.projarc.trabalhofinal.domain.model.reserva.Slot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DisponibilidadeUseCase implements DisponibilidadeInPort {

    private final ReservaPort reservaPort;
    private final RedeAereaPort redeAereaPort;

    @Override
    public Optional<List<Integer>> getAltitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim) {
        return redeAereaPort.buscarAerovia(aeroviaId).map(aerovia -> altitudesDisponiveis(aerovia.getId(), inicio, fim));
    }

    @Override
    public Optional<List<Integer>> getVagasDisponiveis(String codigoAeroporto, LocalDateTime inicio, LocalDateTime fim) {
        return redeAereaPort.buscarAeroporto(codigoAeroporto).map(aeroporto -> vagasDisponiveis(aeroporto, inicio, fim));
    }

    private List<Integer> altitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim) {
        var ocupadas = reservaPort.getReservasDeAerovia(aeroviaId, slots(inicio, fim)).stream()
                .map(ReservaAerovia::getAltitude)
                .collect(Collectors.toSet());
        return Altitudes.todas().stream()
                .filter(altitude -> !ocupadas.contains(altitude))
                .toList();
    }

    private List<Integer> vagasDisponiveis(Aeroporto aeroporto, LocalDateTime inicio, LocalDateTime fim) {
        var ocupadas = reservaPort.getReservasDeEstacionamento(aeroporto.getCodigo(), slots(inicio, fim)).stream()
                .map(ReservaEstacionamento::getNumeroVaga)
                .collect(Collectors.toSet());
        return IntStream.rangeClosed(1, aeroporto.getQtdVagas())
                .boxed()
                .filter(vaga -> !ocupadas.contains(vaga))
                .toList();
    }

    private List<LocalDateTime> slots(LocalDateTime inicio, LocalDateTime fim) {
        return Slot.tocadosPor(inicio, fim).stream().map(Slot::inicio).toList();
    }
}
