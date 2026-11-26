package com.projarc.trabalhofinal.adapter.in.rest.mapper;

import com.projarc.trabalhofinal.adapter.in.rest.dto.reserva.response.AltitudesDisponiveisResponse;
import com.projarc.trabalhofinal.adapter.in.rest.dto.reserva.response.VagasDisponiveisResponse;

import java.time.LocalDateTime;
import java.util.List;

public final class ReservaRestMapper {

    private ReservaRestMapper() {}

    public static AltitudesDisponiveisResponse toAltitudesDisponiveis(String aeroviaId, LocalDateTime inicio, LocalDateTime fim, List<Integer> altitudes) {
        return AltitudesDisponiveisResponse.builder()
                .aeroviaId(aeroviaId)
                .inicio(inicio)
                .fim(fim)
                .altitudes(altitudes)
                .build();
    }

    public static VagasDisponiveisResponse toVagasDisponiveis(String codigoAeroporto, LocalDateTime inicio, LocalDateTime fim, List<Integer> vagas) {
        return VagasDisponiveisResponse.builder()
                .codigoAeroporto(codigoAeroporto)
                .inicio(inicio)
                .fim(fim)
                .vagas(vagas)
                .build();
    }
}
