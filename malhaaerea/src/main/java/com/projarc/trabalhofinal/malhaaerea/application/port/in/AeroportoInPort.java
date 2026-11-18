package com.projarc.trabalhofinal.malhaaerea.application.port.in;

import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;

import java.util.List;

public interface AeroportoInPort {
    List<Aeroporto> getAeroportos();
    Aeroporto getAeroporto(String codigo);
    void createAeroporto(Aeroporto aeroporto);
}
