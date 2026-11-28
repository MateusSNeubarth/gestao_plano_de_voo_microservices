package com.projarc.trabalhofinal.adapter.out.http.malha;

import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;

public class MalhaHttpMapper {

    public static Aeroporto toDomain(AeroportoMalhaResponse response) {
        return Aeroporto.builder()
                .codigo(response.codigo())
                .nome(response.nome())
                .qtdVagas(response.qtdVagas())
                .build();
    }

    public static Aerovia toDomain(AeroviaMalhaResponse response) {
        return Aerovia.builder()
                .id(response.id())
                .codOrigem(response.codOrigem())
                .codDestino(response.codDestino())
                .distanciaKm(response.distanciaKm())
                .build();
    }
}
