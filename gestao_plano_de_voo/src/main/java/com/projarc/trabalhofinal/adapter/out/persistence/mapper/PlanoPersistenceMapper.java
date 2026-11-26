package com.projarc.trabalhofinal.adapter.out.persistence.mapper;

import com.projarc.trabalhofinal.adapter.out.persistence.entity.plano.PlanoDeVooEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.plano.TrechoEntity;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import com.projarc.trabalhofinal.domain.model.plano.Percurso;
import com.projarc.trabalhofinal.domain.model.plano.PlanoDeVoo;
import com.projarc.trabalhofinal.domain.model.plano.Trecho;

import java.util.ArrayList;
import java.util.List;

public final class PlanoPersistenceMapper {
    private PlanoPersistenceMapper() {}

    public static PlanoDeVooEntity fromDomain(PlanoDeVoo planoDeVoo) {
        return PlanoDeVooEntity.builder()
                .id(planoDeVoo.getId())
                .status(planoDeVoo.getStatus())
                .dataHoraPartida(planoDeVoo.getDataHoraPartida())
                .aeronave(AeronavePersistenceMapper.toEntity(planoDeVoo.getAeronave()))
                .piloto(PilotoPersistenceMapper.toEntity(planoDeVoo.getPiloto()))
                .codigoOrigem(planoDeVoo.getOrigem().getCodigo())
                .codigoDestino(planoDeVoo.getDestino().getCodigo())
                .trechos(trechosFromDomain(planoDeVoo.getPercurso().getTrechos()))
                .build();
    }

    public static PlanoDeVoo toDomain(PlanoDeVooEntity planoDeVooEntity) {
        return PlanoDeVoo.builder()
                .id(planoDeVooEntity.getId())
                .status(planoDeVooEntity.getStatus())
                .dataHoraPartida(planoDeVooEntity.getDataHoraPartida())
                .aeronave(AeronavePersistenceMapper.toDomain(planoDeVooEntity.getAeronave()))
                .piloto(PilotoPersistenceMapper.toDomain(planoDeVooEntity.getPiloto()))
                .origem(Aeroporto.builder().codigo(planoDeVooEntity.getCodigoOrigem()).build())
                .destino(Aeroporto.builder().codigo(planoDeVooEntity.getCodigoDestino()).build())
                .percurso(Percurso.builder()
                        .trechos(new ArrayList<>(planoDeVooEntity.getTrechos().stream()
                                .map(PlanoPersistenceMapper::toDomain)
                                .toList()))
                        .build())
                .reservas(new ArrayList<>(planoDeVooEntity.getReservas().stream()
                        .map(ReservaPersistenceMapper::toDomain)
                        .toList()))
                .build();
    }

    public static List<TrechoEntity> trechosFromDomain(List<Trecho> trechos) {
        return new ArrayList<>(trechos.stream()
                .map(PlanoPersistenceMapper::fromDomain)
                .toList());
    }

    public static TrechoEntity fromDomain(Trecho trecho) {
        return TrechoEntity.builder()
                .aeroviaId(trecho.getAerovia().getId())
                .altitude(trecho.getAltitude())
                .horarioEntradaNaAerovia(trecho.getHorarioEntradaNaAerovia())
                .acaoNaChegada(trecho.getAcaoNaChegada())
                .numeroVagaPretendida(trecho.getNumeroVagaPretendida())
                .tempoPermanenciaMinutos(trecho.getTempoPermanenciaMinutos())
                .build();
    }

    public static Trecho toDomain(TrechoEntity trechoEntity) {
        return Trecho.builder()
                // só o id: o PlanoDeVooJpaAdapter completa a aerovia pela RedeAereaPort
                .aerovia(Aerovia.builder().id(trechoEntity.getAeroviaId()).build())
                .altitude(trechoEntity.getAltitude())
                .horarioEntradaNaAerovia(trechoEntity.getHorarioEntradaNaAerovia())
                .acaoNaChegada(trechoEntity.getAcaoNaChegada())
                .numeroVagaPretendida(trechoEntity.getNumeroVagaPretendida())
                .tempoPermanenciaMinutos(trechoEntity.getTempoPermanenciaMinutos())
                .build();
    }
}
