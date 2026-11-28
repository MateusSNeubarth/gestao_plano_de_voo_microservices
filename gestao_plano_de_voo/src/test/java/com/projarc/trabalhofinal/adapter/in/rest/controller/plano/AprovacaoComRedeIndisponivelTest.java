package com.projarc.trabalhofinal.adapter.in.rest.controller.plano;

import com.projarc.trabalhofinal.adapter.out.persistence.repository.ReservaJpaRepository;
import com.projarc.trabalhofinal.application.port.out.AeronavesOutPort;
import com.projarc.trabalhofinal.application.port.out.malha.RedeAereaPort;
import com.projarc.trabalhofinal.application.port.out.plano.PlanoDeVooPort;
import com.projarc.trabalhofinal.domain.exception.RedeAereaIndisponivelException;
import com.projarc.trabalhofinal.domain.model.malha.Aeroporto;
import com.projarc.trabalhofinal.domain.model.malha.Aerovia;
import com.projarc.trabalhofinal.domain.model.plano.AcaoNaChegada;
import com.projarc.trabalhofinal.domain.model.plano.Percurso;
import com.projarc.trabalhofinal.domain.model.plano.Piloto;
import com.projarc.trabalhofinal.domain.model.plano.PlanoDeVoo;
import com.projarc.trabalhofinal.domain.model.plano.Status;
import com.projarc.trabalhofinal.domain.model.plano.Trecho;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:aprovacaoindisponivel;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
class AprovacaoComRedeIndisponivelTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanoDeVooPort planoDeVooPort;

    @Autowired
    private AeronavesOutPort aeronavesOutPort;

    @Autowired
    private ReservaJpaRepository reservaJpaRepository;

    @MockitoBean
    private RedeAereaPort redeAereaPort;

    @Test
    void aprovacaoComRedeIndisponivelResponde503ESemReservas() throws Exception {
        when(redeAereaPort.buscarAerovia("R1")).thenReturn(Optional.of(
                Aerovia.builder().id("R1").distanciaKm(363).codOrigem("POA").codDestino("FLN").build()));
        var id = planoDeVooPort.criarPlano(planoEmEdicao()).getId();
        var reservasAntes = reservaJpaRepository.count();

        var falha = new RedeAereaIndisponivelException("Cadastro da rede aérea indisponível", null);
        when(redeAereaPort.buscarAerovia(anyString())).thenThrow(falha);
        when(redeAereaPort.buscarAeroporto(anyString())).thenThrow(falha);

        mockMvc.perform(post("/api/planos/{id}/aprovacao", id))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));

        assertEquals(reservasAntes, reservaJpaRepository.count());
        doReturn(Optional.empty()).when(redeAereaPort).buscarAerovia(anyString());
        assertEquals(Status.EDICAO, planoDeVooPort.getPlanoDeVoo(id).getStatus());
    }

    private PlanoDeVoo planoEmEdicao() {
        var partida = LocalDateTime.of(2030, 1, 1, 10, 0);
        var trecho = Trecho.builder()
                .aerovia(Aerovia.builder().id("R1").build())
                .altitude(30000)
                .horarioEntradaNaAerovia(partida)
                .acaoNaChegada(AcaoNaChegada.POUSO)
                .build();
        return PlanoDeVoo.builder()
                .status(Status.EDICAO)
                .dataHoraPartida(partida)
                .aeronave(aeronavesOutPort.buscarPorPrefixo("PT-ABC"))
                .piloto(Piloto.builder().id(1).build())
                .origem(Aeroporto.builder().codigo("POA").build())
                .destino(Aeroporto.builder().codigo("FLN").build())
                .percurso(Percurso.builder().trechos(new ArrayList<>(List.of(trecho))).build())
                .build();
    }
}
