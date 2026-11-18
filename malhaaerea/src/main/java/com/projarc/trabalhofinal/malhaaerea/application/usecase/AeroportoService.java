package com.projarc.trabalhofinal.malhaaerea.application.usecase;

import com.projarc.trabalhofinal.malhaaerea.application.port.in.AeroportoInPort;
import com.projarc.trabalhofinal.malhaaerea.application.port.out.AeroportoOutPort;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AeroportoService implements AeroportoInPort {

    private final AeroportoOutPort aeroportoOutPort;

    @Override
    public List<Aeroporto> getAeroportos() {
        return aeroportoOutPort.getAeroportos();
    }

    @Override
    public Aeroporto getAeroporto(String codigo) {
        return aeroportoOutPort.getAeroporto(codigo);
    }

    @Override
    public void createAeroporto(Aeroporto aeroporto) {
        aeroportoOutPort.createAeroporto(aeroporto);
    }
}
