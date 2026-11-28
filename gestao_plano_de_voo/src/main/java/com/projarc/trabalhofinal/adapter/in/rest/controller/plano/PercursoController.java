package com.projarc.trabalhofinal.adapter.in.rest.controller.plano;

import com.projarc.trabalhofinal.adapter.in.rest.dto.plano.request.PercursoRequest;
import com.projarc.trabalhofinal.adapter.in.rest.dto.plano.response.PlanoDeVooResponse;
import com.projarc.trabalhofinal.adapter.in.rest.mapper.PlanoRestMapper;
import com.projarc.trabalhofinal.application.port.in.plano.PlanoDeVooInPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/planos/{id}/percurso")
@RequiredArgsConstructor
public class PercursoController {

    private final PlanoDeVooInPort planoDeVoo;

    @PutMapping
    public ResponseEntity<PlanoDeVooResponse> substituirPercurso(@PathVariable Integer id, @RequestBody PercursoRequest request) {
        var plano = planoDeVoo.substituirPercurso(id, PlanoRestMapper.toDomain(request.trechos()));
        return ResponseEntity.ok(PlanoRestMapper.fromDomain(plano));
    }
}
