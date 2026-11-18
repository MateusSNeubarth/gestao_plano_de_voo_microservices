package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.request;

public record AeroportoRequest(
        String codigo,
        String nome,
        Integer qtdVagas
) {
}
