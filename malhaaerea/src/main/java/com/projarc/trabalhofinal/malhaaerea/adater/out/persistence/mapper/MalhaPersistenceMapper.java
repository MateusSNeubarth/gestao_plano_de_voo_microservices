package com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.mapper;

import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.entity.AeroportoEntity;
import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.entity.AeroviaEntity;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;

public final class MalhaPersistenceMapper {
    private MalhaPersistenceMapper() {
    }

    public static Aeroporto toDomain(AeroportoEntity aeronaveEntity) {
        return Aeroporto.builder()
                .codigo(aeronaveEntity.getCodigo())
                .nome(aeronaveEntity.getNome())
                .qtdVagas(aeronaveEntity.getQtdVagas())
                .build();
    }

    public static AeroportoEntity fromDomain(Aeroporto aeroporto) {
        return AeroportoEntity.builder()
                .codigo(aeroporto.getCodigo())
                .nome(aeroporto.getNome())
                .qtdVagas(aeroporto.getQtdVagas())
                .build();
    }

    public static Aerovia toDomain(AeroviaEntity aeroviaEntity) {
        return Aerovia.builder()
                .id(aeroviaEntity.getId())
                .distanciaKm(aeroviaEntity.getDistanciaKm())
                .codOrigem(aeroviaEntity.getCodigoOrigem())
                .codDestino(aeroviaEntity.getCodigoDestino())
                .build();
    }

    public static AeroviaEntity fromDomain(Aerovia aerovia) {
        return AeroviaEntity.builder()
                .id(aerovia.getId())
                .distanciaKm(aerovia.getDistanciaKm())
                .codigoOrigem(aerovia.getCodOrigem())
                .codigoDestino(aerovia.getCodDestino())
                .build();
    }
}

