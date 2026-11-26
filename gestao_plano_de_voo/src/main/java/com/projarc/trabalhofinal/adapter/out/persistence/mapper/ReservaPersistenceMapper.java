package com.projarc.trabalhofinal.adapter.out.persistence.mapper;

import com.projarc.trabalhofinal.adapter.out.persistence.entity.reserva.ReservaAeroviaEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.reserva.ReservaEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.reserva.ReservaEstacionamentoEntity;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import com.projarc.trabalhofinal.domain.model.reserva.Reserva;
import com.projarc.trabalhofinal.domain.model.reserva.ReservaAerovia;
import com.projarc.trabalhofinal.domain.model.reserva.ReservaEstacionamento;

public final class ReservaPersistenceMapper {
    private ReservaPersistenceMapper() {
    }

    public static Reserva toDomain(ReservaEntity reservaEntity) {
        return switch (reservaEntity) {
            case ReservaAeroviaEntity reservaAerovia -> toDomain(reservaAerovia);
            case ReservaEstacionamentoEntity reservaEstacionamento -> toDomain(reservaEstacionamento);
            default -> throw new IllegalArgumentException("Tipo de reserva desconhecido: " + reservaEntity.getClass().getSimpleName());
        };
    }

    public static ReservaAerovia toDomain(ReservaAeroviaEntity reservaEntity) {
        return ReservaAerovia.builder()
                .id(reservaEntity.getId())
                .slot(reservaEntity.getSlot())
                .aerovia(Aerovia.builder().id(reservaEntity.getAeroviaId()).build())
                .altitude(reservaEntity.getAltitude())
                .build();
    }

    public static ReservaEstacionamento toDomain(ReservaEstacionamentoEntity reservaEntity) {
        return ReservaEstacionamento.builder()
                .id(reservaEntity.getId())
                .slot(reservaEntity.getSlot())
                .aeroporto(Aeroporto.builder().codigo(reservaEntity.getCodigoAeroporto()).build())
                .numeroVaga(reservaEntity.getNumeroVaga())
                .build();
    }

    public static ReservaAeroviaEntity fromDomain(ReservaAerovia reserva) {
        return ReservaAeroviaEntity.builder()
                .slot(reserva.getSlot())
                .aeroviaId(reserva.getAerovia().getId())
                .altitude(reserva.getAltitude())
                .build();
    }

    public static ReservaEstacionamentoEntity fromDomain(ReservaEstacionamento reserva) {
        return ReservaEstacionamentoEntity.builder()
                .slot(reserva.getSlot())
                .codigoAeroporto(reserva.getAeroporto().getCodigo())
                .numeroVaga(reserva.getNumeroVaga())
                .build();
    }
}
