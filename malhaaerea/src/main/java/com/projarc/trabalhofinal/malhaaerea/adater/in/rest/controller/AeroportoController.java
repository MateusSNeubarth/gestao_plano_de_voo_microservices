package com.projarc.trabalhofinal.malhaaerea.adater.in.rest.controller;

import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.request.AeroportoRequest;
import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.dto.response.AeroportoResponse;
import com.projarc.trabalhofinal.malhaaerea.adater.in.rest.mapper.MalhaRestMapper;
import com.projarc.trabalhofinal.malhaaerea.application.port.in.AeroportoInPort;
import com.projarc.trabalhofinal.malhaaerea.domain.exception.ConflictException;
import com.projarc.trabalhofinal.malhaaerea.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aeroportos")
@RequiredArgsConstructor
public class AeroportoController {

    private final AeroportoInPort aeronaves;

    @GetMapping
    public ResponseEntity<List<AeroportoResponse>> getAeroportos() {
        return ResponseEntity.ok(
                aeronaves.getAeroportos().stream()
                        .map(MalhaRestMapper::fromDomain)
                        .toList()
        );
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<AeroportoResponse> getAeroporto(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(MalhaRestMapper.fromDomain(aeronaves.getAeroporto(codigo)));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> postAeroporto(@RequestBody AeroportoRequest request) {
        try {
            this.aeronaves.createAeroporto(MalhaRestMapper.toDomain(request));
            return ResponseEntity.created(URI.create("/api/aeroportos/" + request.codigo())).build();
        } catch(ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
