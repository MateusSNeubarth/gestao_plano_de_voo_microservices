package com.projarc.trabalhofinal.malhaaerea.application.port.in;

import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;

import java.util.List;

public interface AeroviaInPort {
    List<Aerovia> getAerovias();
    Aerovia getAerovia(String id);
    void createAerovia(Aerovia aerovia);
}
