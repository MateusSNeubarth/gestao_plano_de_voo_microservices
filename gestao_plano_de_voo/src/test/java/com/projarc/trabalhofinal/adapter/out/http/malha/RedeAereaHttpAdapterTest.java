package com.projarc.trabalhofinal.adapter.out.http.malha;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.projarc.trabalhofinal.domain.exception.RedeAereaIndisponivelException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedeAereaHttpAdapterTest {

    private WireMockServer malha;
    private RedeAereaHttpAdapter adapter;

    @BeforeEach
    void setUp() {
        malha = new WireMockServer(wireMockConfig().dynamicPort());
        malha.start();
        var restClient = MalhaHttpConfig.builder(Duration.ofMillis(500), Duration.ofMillis(300))
                .baseUrl(malha.baseUrl())
                .build();
        adapter = new RedeAereaHttpAdapter(restClient);
    }

    @AfterEach
    void tearDown() {
        malha.stop();
    }

    @Test
    void sucesso() {
        malha.stubFor(get("/api/aerovias/R1")
                .willReturn(okJson("{\"id\":\"R1\",\"codOrigem\":\"POA\",\"codDestino\":\"FLN\",\"distanciaKm\":363}")));
        malha.stubFor(get("/api/aeroportos/POA")
                .willReturn(okJson("{\"codigo\":\"POA\",\"nome\":\"Salgado Filho\",\"qtdVagas\":10}")));

        var aerovia = adapter.buscarAerovia("R1").orElseThrow();
        assertEquals(363, aerovia.getDistanciaKm());
        assertEquals("POA", aerovia.getCodOrigem());
        assertEquals("FLN", aerovia.getCodDestino());
        assertEquals(10, adapter.buscarAeroporto("POA").orElseThrow().getQtdVagas());
    }

    @Test
    void naoEncontradoViraVazio() {
        malha.stubFor(get("/api/aerovias/R99").willReturn(aResponse().withStatus(404)));

        assertTrue(adapter.buscarAerovia("R99").isEmpty());
    }

    @Test
    void erroDoServidorViraIndisponivel() {
        malha.stubFor(get("/api/aeroportos/POA").willReturn(aResponse().withStatus(500)));

        assertThrows(RedeAereaIndisponivelException.class, () -> adapter.buscarAeroporto("POA"));
    }

    @Test
    void timeoutViraIndisponivel() {
        malha.stubFor(get("/api/aerovias/R1").willReturn(okJson("{}").withFixedDelay(1000)));

        assertThrows(RedeAereaIndisponivelException.class, () -> adapter.buscarAerovia("R1"));
    }

    @Test
    void conexaoRecusadaViraIndisponivel() {
        malha.stop();

        assertThrows(RedeAereaIndisponivelException.class, () -> adapter.buscarAerovia("R1"));
    }
}
