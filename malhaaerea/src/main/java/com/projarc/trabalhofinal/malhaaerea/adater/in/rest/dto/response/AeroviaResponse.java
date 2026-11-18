package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.response;

import lombok.Builder;

@Builder
public record AeroviaResponse(
        String id,
        String codOrigem,
        String codDestino,
        Integer distanciaKm
) {
}
