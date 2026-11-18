package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.mapper;

import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.request.AeroportoRequest;
import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.request.AeroviaRequest;
import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.response.AeroportoResponse;
import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.response.AeroviaResponse;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;

public final class MalhaRestMapper {

    private MalhaRestMapper() {}

    public static AeroportoResponse fromDomain(Aeroporto aeroporto) {
        return AeroportoResponse.builder()
                .codigo(aeroporto.getCodigo())
                .nome(aeroporto.getNome())
                .qtdVagas(aeroporto.getQtdVagas())
                .build();
    }

    public static Aeroporto toDomain(AeroportoRequest request) {
        return Aeroporto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .qtdVagas(request.qtdVagas())
                .build();
    }

    public static AeroviaResponse fromDomain(Aerovia aerovia){
        return AeroviaResponse.builder()
                .id(aerovia.getId())
                .codOrigem(aerovia.getCodOrigem())
                .codDestino(aerovia.getCodDestino())
                .distanciaKm(aerovia.getDistanciaKm())
                .build();
    }

    public static Aerovia toDomain(AeroviaRequest request) {
        return Aerovia.builder().id(request.id())
                .distanciaKm(request.distanciaKm())
                .codOrigem(request.codOrigem())
                .codDestino(request.codDestino())
                .build();
    }
}

