package com.projarc.trabalhofinal.adapter.in.rest.controller.plano;

import com.projarc.trabalhofinal.adapter.in.rest.dto.plano.request.PlanoDeVooRequest;
import com.projarc.trabalhofinal.adapter.in.rest.dto.plano.response.PlanoDeVooResponse;
import com.projarc.trabalhofinal.adapter.in.rest.mapper.PlanoRestMapper;
import com.projarc.trabalhofinal.application.port.in.AeronaveInPort;
import com.projarc.trabalhofinal.application.port.in.plano.PlanoDeVooInPort;
import com.projarc.trabalhofinal.domain.model.plano.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/planos")
@RequiredArgsConstructor
public class PlanoDeVooController {

    private final PlanoDeVooInPort planoDeVoo;
    private final AeronaveInPort aeronaveInPort;

    @PostMapping
    public ResponseEntity<PlanoDeVooResponse> criarPlano(@RequestBody PlanoDeVooRequest request) {
        var aeronave = request.aeronavePrefixo() == null ? null : aeronaveInPort.buscarPorPrefixo(request.aeronavePrefixo());
        if (aeronave == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        var criado = planoDeVoo.criarPlano(PlanoRestMapper.toDomain(request, aeronave));
        return ResponseEntity
                .created(URI.create("/api/planos/" + criado.getId()))
                .body(PlanoRestMapper.fromDomain(criado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoDeVooResponse> consultarPlano(@PathVariable Integer id) {
        return ResponseEntity.ok(PlanoRestMapper.fromDomain(planoDeVoo.consultarPlano(id)));
    }

    @GetMapping
    public ResponseEntity<List<PlanoDeVooResponse>> listarPorSituacao(@RequestParam Status situacao) {
        return ResponseEntity.ok(planoDeVoo.listarPlanosPorStatus(situacao).stream()
                .map(PlanoRestMapper::fromDomain)
                .toList());
    }
}
