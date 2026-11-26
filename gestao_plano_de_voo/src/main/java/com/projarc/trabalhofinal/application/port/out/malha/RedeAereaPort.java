package com.projarc.trabalhofinal.application.port.out.malha;

import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;

import java.util.Optional;

/**
 * Contrato C1: tudo que a gestão consome da rede aérea.
 * Vazio significa "não existe"; falha de comunicação lança {@code RedeAereaIndisponivelException}.
 */
public interface RedeAereaPort {

    Optional<Aeroporto> buscarAeroporto(String codigo);

    Optional<Aerovia> buscarAerovia(String id);
}
