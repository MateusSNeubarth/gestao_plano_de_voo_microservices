package com.projarc.trabalhofinal.adapter.out.persistence.adapter.plano;

import com.projarc.trabalhofinal.application.port.out.AeronavesOutPort;
import com.projarc.trabalhofinal.application.port.out.plano.PlanoDeVooPort;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:planoadapter;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
class PlanoDeVooJpaAdapterTest {

    @Autowired
    private PlanoDeVooPort planoDeVooPort;

    @Autowired
    private AeronavesOutPort aeronavesOutPort;

    @Test
    void leituraCompletaAAeroviaPelaRedeAerea() {
        var partida = LocalDateTime.of(2030, 1, 1, 10, 0);
        var trecho = Trecho.builder()
                .aerovia(Aerovia.builder().id("R1").build())
                .altitude(30000)
                .horarioEntradaNaAerovia(partida)
                .acaoNaChegada(AcaoNaChegada.POUSO)
                .build();
        var plano = PlanoDeVoo.builder()
                .status(Status.EDICAO)
                .dataHoraPartida(partida)
                .aeronave(aeronavesOutPort.buscarPorPrefixo("PT-ABC"))
                .piloto(Piloto.builder().id(1).build())
                .origem(Aeroporto.builder().codigo("POA").build())
                .destino(Aeroporto.builder().codigo("FLN").build())
                .percurso(Percurso.builder().trechos(new ArrayList<>(List.of(trecho))).build())
                .build();

        var lido = planoDeVooPort.getPlanoDeVoo(planoDeVooPort.criarPlano(plano).getId());

        var aerovia = lido.getPercurso().getTrechos().getFirst().getAerovia();
        assertEquals(363, aerovia.getDistanciaKm());
        assertEquals("POA", aerovia.getCodOrigem());
        assertEquals("FLN", aerovia.getCodDestino());
        assertEquals("FLN", lido.getDestino().getCodigo());
    }
}
