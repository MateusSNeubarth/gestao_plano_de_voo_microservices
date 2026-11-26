package com.projarc.trabalhofinal.adapter.out.persistence.adapter.plano;

import com.projarc.trabalhofinal.adapter.out.persistence.entity.aeronave.AeronaveEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.plano.PilotoEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.entity.plano.PlanoDeVooEntity;
import com.projarc.trabalhofinal.adapter.out.persistence.mapper.PlanoPersistenceMapper;
import com.projarc.trabalhofinal.adapter.out.persistence.repository.AeronaveJpaRepository;
import com.projarc.trabalhofinal.adapter.out.persistence.repository.PilotoJpaRepository;
import com.projarc.trabalhofinal.adapter.out.persistence.repository.PlanoDeVooJpaRepository;
import com.projarc.trabalhofinal.application.port.out.malha.RedeAereaPort;
import com.projarc.trabalhofinal.application.port.out.plano.PlanoDeVooPort;
import com.projarc.trabalhofinal.domain.exception.PlanoNaoEncontradoException;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import com.projarc.trabalhofinal.domain.model.plano.Percurso;
import com.projarc.trabalhofinal.domain.model.plano.PlanoDeVoo;
import com.projarc.trabalhofinal.domain.model.plano.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanoDeVooJpaAdapter implements PlanoDeVooPort {

    private final PlanoDeVooJpaRepository planoDeVooJpaRepository;
    private final PilotoJpaRepository pilotoJpaRepository;
    private final RedeAereaPort redeAereaPort;
    private final AeronaveJpaRepository aeronaveJpaRepository;

    @Override
    @Transactional
    public PlanoDeVoo criarPlano(PlanoDeVoo plano) {
        PlanoDeVooEntity planoEntity = PlanoPersistenceMapper.fromDomain(plano);

        AeronaveEntity aeronaveEntity = aeronaveJpaRepository
                .findById(plano.getAeronave().getPrefixo())
                .orElseThrow(() -> new IllegalArgumentException("Aeronave não encontrada"));

        PilotoEntity pilotoEntity = pilotoJpaRepository
                .findById(plano.getPiloto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Piloto não encontrado"));

        planoEntity.setAeronave(aeronaveEntity);
        planoEntity.setPiloto(pilotoEntity);

        PlanoDeVooEntity planoSalvo = planoDeVooJpaRepository.save(planoEntity);
        return toDomain(planoSalvo);
    }

    @Override
    @Transactional
    public List<PlanoDeVoo> listarPlanosPorStatus(Status status) {
        return planoDeVooJpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public PlanoDeVoo getPlanoDeVoo(Integer id) {
        return planoDeVooJpaRepository.findById(id).map(this::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public PlanoDeVoo updatePercurso(Integer id, Percurso percurso) {
        var plano = planoDeVooJpaRepository.findById(id).orElseThrow(() -> new PlanoNaoEncontradoException(id));
        var trechos = PlanoPersistenceMapper.trechosFromDomain(percurso.getTrechos());
        plano.getTrechos().clear();
        plano.getTrechos().addAll(trechos);
        return toDomain(planoDeVooJpaRepository.saveAndFlush(plano));
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, Status status) {
        var plano = planoDeVooJpaRepository.findById(id).orElseThrow(() -> new PlanoNaoEncontradoException(id));
        plano.setStatus(status);
        planoDeVooJpaRepository.save(plano);
    }

    // O banco guarda só o id da aerovia; distância e extremos vêm da rede aérea (uma busca por id).
    // Aerovia que deixou de existir fica só com o id e as verificações do plano apontam o problema.
    private PlanoDeVoo toDomain(PlanoDeVooEntity entity) {
        var plano = PlanoPersistenceMapper.toDomain(entity);
        var aerovias = new HashMap<String, Aerovia>();
        plano.getPercurso().getTrechos().forEach(trecho -> trecho.setAerovia(aerovias.computeIfAbsent(
                trecho.getAerovia().getId(),
                id -> redeAereaPort.buscarAerovia(id).orElse(trecho.getAerovia()))));
        return plano;
    }
}
