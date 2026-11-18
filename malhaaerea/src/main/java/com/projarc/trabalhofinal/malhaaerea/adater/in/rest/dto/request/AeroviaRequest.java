package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.request;

public record AeroviaRequest(
        String id,
        String codOrigem,
        String codDestino,
        Integer distanciaKm
) {
}

