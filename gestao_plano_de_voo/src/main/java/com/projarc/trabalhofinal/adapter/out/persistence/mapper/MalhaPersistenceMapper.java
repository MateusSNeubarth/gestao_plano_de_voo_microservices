package com.projarc.trabalhofinal.adapter.out.persistence.mapper;

import com.projarc.trabalhofinal.adapter.out.persistence.entity.malha.AeroportoEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.malha.AeroviaEntity;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;

public final class MalhaPersistenceMapper {
    private MalhaPersistenceMapper() {
    }

    public static Aeroporto toDomain(AeroportoEntity aeroportoEntity) {
        return Aeroporto.builder()
                .codigo(aeroportoEntity.getCodigo())
                .nome(aeroportoEntity.getNome())
                .qtdVagas(aeroportoEntity.getQtdVagas())
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
}
