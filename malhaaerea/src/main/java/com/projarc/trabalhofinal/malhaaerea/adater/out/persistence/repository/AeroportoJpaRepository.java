package com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.repository;

import com.projarc.trabalhofinal.malhaaerea.adater.out.persistence.entity.AeroportoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AeroportoJpaRepository extends JpaRepository<AeroportoEntity, String> {
    Optional<AeroportoEntity> findByCodigo(String codigo);

    Boolean existsByCodigo(String codigo);
}
