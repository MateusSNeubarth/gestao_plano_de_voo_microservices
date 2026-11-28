# ADR 004: Integração HTTP da gestão com o cadastro da rede aérea

**Status:** aceita (substitui a implementação local da ADR 002)

## Contexto

Pela ADR 002, a gestão lê aeroportos e aerovias só pela `RedeAereaPort`, mas até aqui a implementação lia tabelas locais copiadas da Parte 1. Agora o `malha-aerea` é o dono desses dados e a gestão passa a consultá-lo pela rede. Uma falha de comunicação não pode resultar em plano aprovado com dados incompletos, e os planos e reservas existentes não podem ser perdidos.

## Decisão

**Integração síncrona por HTTP.** O `RedeAereaHttpAdapter` (`adapter/out/http/malha/`) implementa a `RedeAereaPort` com `RestClient` sobre `GET /api/aeroportos/{codigo}` e `GET /api/aerovias/{id}` em `http://malha-aerea`, resolvido pelo Eureka. Validar e aprovar um plano dependem do dado atual do cadastro, então uma cópia assíncrona (por eventos) traria o risco de aprovar com dado desatualizado. Os DTOs de transporte (`AeroportoMalhaResponse`, `AeroviaMalhaResponse`) e o mapper ficam no adaptador; o domínio não muda.

**Load balancer sem bean `RestClient.Builder`.** O interceptor do Spring Cloud LoadBalancer (`DeferringLoadBalancerInterceptor`) é aplicado direto no `RestClient` do `malha-aerea`, em `MalhaHttpConfig`. Um bean `@LoadBalanced RestClient.Builder` foi descartado: o cliente do Eureka passa a usar esse builder e tenta resolver o próprio servidor do Eureka pelo load balancer (`No instances available for localhost`), e a gestão não se registra.

**Timeouts por propriedade.**

| Propriedade | Padrão | Variável de ambiente |
|---|---|---|
| `malha.timeout.connect` | 1s | `MALHA_CONNECT_TIMEOUT` |
| `malha.timeout.read` | 2s | `MALHA_READ_TIMEOUT` |

Os valores ficam abaixo do `response-timeout: 5s` do gateway, para que a gestão responda 503 antes que o gateway desista. A leitura de um plano faz uma chamada por aerovia distinta, em sequência; um plano com três aerovias lentas pode passar dos 5s (ver pontos em aberto).

**"Não existe" e "indisponível" são diferentes.**

| Resposta do `malha-aerea` | Na `RedeAereaPort` | Na API da gestão |
|---|---|---|
| 200 | objeto de domínio | segue o fluxo |
| 404 | `Optional.empty()` | 422 nas regras que exigem o recurso (ex.: "Aerovia não encontrada: R99"); na leitura do plano, a aerovia fica só com o id e as verificações apontam o problema |
| timeout, conexão recusada, 5xx, outro 4xx, resposta ilegível, nenhuma instância no Eureka | `RedeAereaIndisponivelException` | 503 `application/problem+json` |

**Tratamento de erros global.** O `TratamentoDeErros` (`@RestControllerAdvice`, em `adapter/in/rest/config/erro/`) concentra os handlers que estavam repetidos nos controllers de plano, com os mesmos status (404, 409, 422), e mapeia `RedeAereaIndisponivelException` para 503 com `ProblemDetail`.

**Aprovação com o cadastro indisponível.** A aprovação lê o plano (o que busca as aerovias no cadastro) e o valida antes de reservar, tudo numa única transação. Se o cadastro está fora, a exceção ocorre antes de qualquer reserva e a transação é desfeita: o plano continua em edição, nenhuma reserva é gravada e a resposta é 503.

**Remoção do cadastro local.** Saíram da gestão o `RedeAereaJpaAdapter`, as entidades e repositórios de aeroporto e aerovia e o `MalhaPersistenceMapper`. A `V40__remover_cadastro_malha.sql` executa `DROP TABLE AEROVIA, AEROPORTO CASCADE`, que remove também as FKs sem nome criadas em V6/V7 (o comportamento foi verificado no H2). As V1 a V4 continuam no histórico do Flyway da gestão.

**Dados iniciais no `malha-aerea`.** O `malha-aerea` recebe as mesmas V1 a V4 (POA, FLN e CWB; R1, R4, R5 e R8), então os planos da Parte 1 continuam encontrando suas aerovias.

**Preservação dos planos existentes.** `PLANO_DE_VOO`, `TRECHO` e `RESERVA` mantêm as colunas de código (`CODIGO_ORIGEM`, `CODIGO_DESTINO`, `AEROVIA_ID`, `CODIGO_AEROPORTO`); nenhum dado de plano ou reserva é alterado. Aeroportos e aerovias cadastrados por REST no banco da gestão, além dos dados iniciais, não são migrados para o `malha-aerea`: um plano que use uma dessas aerovias continua legível, mas a validação aponta que ela não existe até que seja cadastrada no `malha-aerea`.

## Pontos em aberto

- **Credencial técnica adiada.** O `malha-aerea` não tem segurança nesta versão, e a gestão o chama sem autenticação. A credencial técnica (`MALHA_USERNAME`/`MALHA_PASSWORD`) entra junto com a segurança no gateway.
- **Uma chamada por aerovia em cada leitura de plano.** Se a latência virar problema: buscar várias aerovias numa só chamada, ou fazer cache curto na gestão.
- **Gateway com instância morta ainda registrada.** Enquanto o Eureka ainda lista o `malha-aerea` parado, o gateway responde 500 (`Connection refused`) em `/malha/**`; depois que a instância sai do registro, responde 503. Fica para o circuit breaker do gateway.
- **Latência do Eureka.** Uma instância parada pode levar até cerca de 90s para sair do registro, e uma nova leva cerca de 30s para ser vista pelos clientes. Nesse intervalo, a gestão pode responder 503 com o `malha-aerea` já no ar.
- **`qtdVagas` em vez de `List<Integer> vagas`.** Continua como na ADR 002.

## Consequências

- Uma aerovia nova cadastrada no `malha-aerea` pode ser usada num plano sem mudar a gestão.
- A gestão depende do `malha-aerea` para validar, aprovar e ler planos e para consultar disponibilidade. Com ele fora, essas operações respondem 503, e cancelar ou abandonar também, porque primeiro leem o plano.
- O adaptador tem testes com WireMock (sucesso, 404, 500, timeout e conexão recusada). Os testes que precisam da rede aérea usam a `RedeAereaPort` mockada.
