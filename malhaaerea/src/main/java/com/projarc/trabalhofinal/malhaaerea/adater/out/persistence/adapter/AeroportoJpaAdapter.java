package com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.adapter;

import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.entity.AeroportoEntity;
import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.mapper.MalhaPersistenceMapper;
import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.repository.AeroportoJpaRepository;
import com.projarc.trabalhofinal.malhaaerea.application.port.out.AeroportoOutPort;
import com.projarc.trabalhofinal.malhaaerea.domain.exception.ConflictException;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aeroporto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AeroportoJpaAdapter implements AeroportoOutPort {

    private final AeroportoJpaRepository aeroportoJpaRepository;

    @Override
    public List<Aeroporto> getAeroportos() {
        return aeroportoJpaRepository.findAll().stream()
                .map(MalhaPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Aeroporto getAeroporto(String codigo) {
        return aeroportoJpaRepository.findByCodigo(codigo).map(MalhaPersistenceMapper::toDomain).orElse(null);
    }

    @Override
    public void createAeroporto(Aeroporto aeroporto) {
        Optional<AeroportoEntity> exists = aeroportoJpaRepository.findByCodigo(aeroporto.getCodigo());
        if (exists.isPresent()) throw new ConflictException("Aeroporto já existe");

        aeroportoJpaRepository.save(MalhaPersistenceMapper.fromDomain(aeroporto));
    }
}
