package com.projarc.trabalhofinal.adapter.out.persistence.adapter.malha;

import com.projarc.trabalhofinal.adapter.out.persistence.mapper.MalhaPersistenceMapper;
import com.projarc.trabalhofinal.adapter.out.persistence.repository.AeroportoJpaRepository;
import com.projarc.trabalhofinal.adapter.out.persistence.repository.AeroviaJpaRepository;
import com.projarc.trabalhofinal.application.port.out.malha.RedeAereaPort;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

// shortcut: lê as tabelas locais da Parte 1, substituir pelo adaptador HTTP do malha-aerea na E3-02 (#11)
@Component
@RequiredArgsConstructor
public class RedeAereaJpaAdapter implements RedeAereaPort {

    private final AeroportoJpaRepository aeroportoJpaRepository;
    private final AeroviaJpaRepository aeroviaJpaRepository;

    @Override
    public Optional<Aeroporto> buscarAeroporto(String codigo) {
        return aeroportoJpaRepository.findById(codigo).map(MalhaPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Aerovia> buscarAerovia(String id) {
        return aeroviaJpaRepository.findById(id).map(MalhaPersistenceMapper::toDomain);
    }
}
