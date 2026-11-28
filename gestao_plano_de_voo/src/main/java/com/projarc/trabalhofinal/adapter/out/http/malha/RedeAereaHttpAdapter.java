package com.projarc.trabalhofinal.adapter.out.http.malha;

import com.projarc.trabalhofinal.application.port.out.malha.RedeAereaPort;
import com.projarc.trabalhofinal.domain.exception.RedeAereaIndisponivelException;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedeAereaHttpAdapter implements RedeAereaPort {

    private final RestClient malhaRestClient;

    @Override
    public Optional<Aeroporto> buscarAeroporto(String codigo) {
        return buscar("/api/aeroportos/{codigo}", codigo, AeroportoMalhaResponse.class).map(MalhaHttpMapper::toDomain);
    }

    @Override
    public Optional<Aerovia> buscarAerovia(String id) {
        return buscar("/api/aerovias/{id}", id, AeroviaMalhaResponse.class).map(MalhaHttpMapper::toDomain);
    }

    private <T> Optional<T> buscar(String uri, String chave, Class<T> tipo) {
        try {
            return Optional.ofNullable(malhaRestClient.get().uri(uri, chave).retrieve().body(tipo));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException | IllegalStateException e) {
            // IllegalStateException: o load balancer não achou instância do malha-aerea no Eureka
            throw new RedeAereaIndisponivelException("Cadastro da rede aérea indisponível", e);
        }
    }
}
