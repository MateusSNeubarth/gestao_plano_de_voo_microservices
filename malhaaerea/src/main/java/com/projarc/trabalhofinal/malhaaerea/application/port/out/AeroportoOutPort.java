package com.projarc.trabalhofinal.malhaaerea.application.port.out;

import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;

import java.util.List;

public interface AeroportoOutPort {
    List<Aeroporto> getAeroportos();
    Aeroporto getAeroporto(String codigo);
    void createAeroporto(Aeroporto aeroporto);
}
