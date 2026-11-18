package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.response;

import lombok.Builder;

@Builder
public record AeroportoResponse(
        String codigo,
        String nome,
        Integer qtdVagas
) {
}
