package com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.adapter;

import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.mapper.MalhaPersistenceMapper;
import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.repository.AeroviaJpaRepository;
import com.projarc.trabalhofinal.malhaaerea.application.port.out.AeroviaOutPort;
import com.projarc.trabalhofinal.malhaaerea.domain.model.Aerovia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AeroviaJpaAdapter implements AeroviaOutPort {

    private final AeroviaJpaRepository repository;

    @Override
    public List<Aerovia> getAerovias() {
        return repository.findAll().stream()
                .map(MalhaPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Aerovia getAerovia(String id) {
        return repository.findById(id).map(MalhaPersistenceMapper::toDomain).orElse(null);
    }

    @Override
    public void createAerovia(Aerovia aerovia) {
        repository.save(MalhaPersistenceMapper.fromDomain(aerovia));
    }
}
