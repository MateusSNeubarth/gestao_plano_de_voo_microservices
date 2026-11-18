package com.projarc.trabalhofinal.malhaaerea.application.usecase;

import com.projarc.trabalhofinal.malhaaerea.application.port.in.AeroviaInPort;
import com.projarc.trabalhofinal.malhaaerea.application.port.out.AeroviaOutPort;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AeroviaService implements AeroviaInPort {

    private final AeroviaOutPort aeroviaOutPort;

    @Override
    public List<Aerovia> getAerovias() {
        return aeroviaOutPort.getAerovias();
    }

    @Override
    public Aerovia getAerovia(String id) {
        return aeroviaOutPort.getAerovia(id);
    }

    @Override
    public void createAerovia(Aerovia aerovia) {
        aeroviaOutPort.createAerovia(aerovia);
    }
}
