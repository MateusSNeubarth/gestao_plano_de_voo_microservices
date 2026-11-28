package com.projarc.trabalhofinal.gateway;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "eureka.client.enabled=false",
        "spring.http.clients.read-timeout=2s"
})
class GatewayRoteamentoTest {

    // serviço falso da gestão; malha-aerea fica sem instância de propósito
    static final HttpServer gestao = criarGestaoFalsa();
    static final HttpClient http = HttpClient.newHttpClient();

    @LocalServerPort
    int porta;

    @DynamicPropertySource
    static void instancias(DynamicPropertyRegistry registro) {
        registro.add("spring.cloud.discovery.client.simple.instances.gestao-voo[0].uri",
                () -> "http://localhost:" + gestao.getAddress().getPort());
    }

    @AfterAll
    static void parar() {
        gestao.stop(0);
    }

    static final String USUARIO = "Basic dXN1YXJpbzp1c3VhcmlvMTIz"; // usuario:usuario123
    static final String ADMIN = "Basic YWRtaW46YWRtaW4xMjM="; // admin:admin123

    @Test
    void reescreveCaminhoETrocaCredencialPelaIdentidade() throws Exception {
        // X-Usuario forjado pelo cliente é descartado; Authorization não segue para o serviço
        var resposta = enviar("GET", "/gestao/planos?x=1", USUARIO, "X-Usuario", "admin", "X-Papel", "ADMIN");
        assertThat(resposta.statusCode()).isEqualTo(200);
        assertThat(resposta.body()).isEqualTo("/api/planos?x=1|usuario|USUARIO|");
    }

    @Test
    void semCredencialOuSenhaErradaResponde401() throws Exception {
        assertThat(enviar("GET", "/gestao/planos", null).statusCode()).isEqualTo(401);
        assertThat(enviar("GET", "/gestao/planos", "Basic dXN1YXJpbzplcnJhZGE=").statusCode()).isEqualTo(401);
    }

    @Test
    void cadastroSoParaAdmin() throws Exception {
        assertThat(enviar("POST", "/gestao/aeronaves", USUARIO).statusCode()).isEqualTo(403);
        assertThat(enviar("POST", "/malha/aerovias", USUARIO).statusCode()).isEqualTo(403);
        assertThat(enviar("GET", "/gestao/aeronaves", USUARIO).statusCode()).isEqualTo(200);
        assertThat(enviar("POST", "/gestao/aeronaves", ADMIN).body()).isEqualTo("/api/aeronaves|admin|ADMIN|");
    }

    @Test
    void docsSemCredencial() throws Exception {
        var resposta = enviar("GET", "/gestao/v3/api-docs", null);
        assertThat(resposta.statusCode()).isEqualTo(200);
        assertThat(resposta.body()).isEqualTo("/v3/api-docs|||");
    }

    @Test
    void erroDoServicoPassaSemAlteracao() throws Exception {
        var resposta = enviar("GET", "/gestao/erro", USUARIO);
        assertThat(resposta.statusCode()).isEqualTo(422);
        assertThat(resposta.headers().firstValue("Content-Type")).hasValue("application/problem+json");
        assertThat(resposta.body()).isEqualTo("{\"status\":422,\"detail\":\"regra\"}");
    }

    @Test
    void respostaLentaDentroDoTimeoutNaoCaiNoFallback() throws Exception {
        // 1,5s: passaria do TimeLimiter padrão (1s) do Resilience4j
        assertThat(enviar("GET", "/gestao/lento", USUARIO).statusCode()).isEqualTo(200);
    }

    @Test
    void timeoutDoDestinoResponde504() throws Exception {
        var resposta = enviar("GET", "/gestao/muito-lento", USUARIO);
        assertThat(resposta.statusCode()).isEqualTo(504);
        assertThat(resposta.headers().firstValue("Content-Type")).hasValue("application/problem+json");
        assertThat(resposta.body()).contains("\"servico\":\"gestao-voo\"");
    }

    @Test
    void destinoSemInstanciaResponde503() throws Exception {
        var resposta = enviar("GET", "/malha/aeroportos", USUARIO);
        assertThat(resposta.statusCode()).isEqualTo(503);
        assertThat(resposta.headers().firstValue("Content-Type")).hasValue("application/problem+json");
        assertThat(resposta.body()).contains("\"status\":503", "\"servico\":\"malha-aerea\"");
    }

    private HttpResponse<String> enviar(String metodo, String caminho, String authorization, String... cabecalhos) throws Exception {
        var requisicao = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + caminho))
                .method(metodo, HttpRequest.BodyPublishers.noBody());
        if (authorization != null) {
            requisicao.header("Authorization", authorization);
        }
        if (cabecalhos.length > 0) {
            requisicao.headers(cabecalhos);
        }
        return http.send(requisicao.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static HttpServer criarGestaoFalsa() {
        try {
            HttpServer servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            servidor.createContext("/", GatewayRoteamentoTest::responder);
            servidor.setExecutor(Executors.newVirtualThreadPerTaskExecutor()); // sem isso, uma resposta lenta trava as seguintes
            servidor.start();
            return servidor;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void responder(HttpExchange troca) throws IOException {
        String caminho = troca.getRequestURI().toString();
        try {
            switch (caminho) {
                case "/api/erro" -> {
                    troca.getResponseHeaders().add("Content-Type", "application/problem+json");
                    escrever(troca, 422, "{\"status\":422,\"detail\":\"regra\"}");
                }
                case "/api/lento" -> { Thread.sleep(1500); escrever(troca, 200, "ok"); }
                case "/api/muito-lento" -> { Thread.sleep(3000); escrever(troca, 200, "tarde"); }
                default -> {
                    var h = troca.getRequestHeaders();
                    escrever(troca, 200, String.join("|", caminho, valor(h.getFirst("X-Usuario")),
                            valor(h.getFirst("X-Papel")), valor(h.getFirst("Authorization"))));
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException ignorado) {
            // o gateway já desistiu da resposta (teste de timeout)
        }
    }

    private static String valor(String v) {
        return v == null ? "" : v;
    }

    private static void escrever(HttpExchange troca, int status, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        troca.sendResponseHeaders(status, bytes.length);
        troca.getResponseBody().write(bytes);
        troca.close();
    }
}
