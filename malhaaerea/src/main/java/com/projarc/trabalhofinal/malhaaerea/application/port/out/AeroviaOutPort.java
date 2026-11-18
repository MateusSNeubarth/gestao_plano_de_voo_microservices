package com.projarc.trabalhofinal.malhaaerea.application.port.out;

import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;

import java.util.List;

public interface AeroviaOutPort {
    List<Aerovia> getAerovias();
    Aerovia getAerovia(String id);
    void createAerovia(Aerovia aerovia);
}
