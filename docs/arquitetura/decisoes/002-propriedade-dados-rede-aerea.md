# ADR 002: Propriedade dos dados da rede aérea na gestão

**Status:** aceita

## Contexto

Aeroportos e aerovias passam a pertencer ao serviço `malha-aerea`. A gestão de planos precisa deles para validar e aprovar planos, mas não pode mais depender das entidades JPA nem das tabelas da malha. Planos e reservas existentes da Parte 1 não podem ser perdidos.

## Decisão

**Uma porta só para a rede aérea.** Toda leitura de aeroporto ou aerovia na gestão passa por `RedeAereaPort` (contrato C1): `buscarAeroporto(codigo)` e `buscarAerovia(id)`, com `Optional` vazio quando o recurso não existe. Falha de comunicação lança `RedeAereaIndisponivelException` (usada a partir da E3-02). Nesta etapa a implementação é `RedeAereaJpaAdapter`, somente leitura, sobre as tabelas locais `AEROPORTO` e `AEROVIA`.

**A gestão guarda só identificadores.** `PLANO_DE_VOO`, `TRECHO` e `RESERVA` continuam com as mesmas colunas (`CODIGO_ORIGEM`, `CODIGO_DESTINO`, `AEROVIA_ID`, `CODIGO_AEROPORTO`), agora mapeadas como `String` nas entidades, sem `@ManyToOne`. Nenhum dado é migrado.

**Quem completa os objetos de domínio.** O domínio precisa da distância e dos extremos da aerovia (`Trecho.chegada`, verificações, resposta REST). O `PlanoDeVooJpaAdapter` completa as aerovias dos trechos pela `RedeAereaPort` ao ler um plano, com uma busca por aerovia distinta. É o único ponto por onde passam todas as leituras de plano; hidratar nos casos de uso exigiria repetir a lógica em consulta, listagem, validação e aprovação. Aerovia que deixou de existir fica só com o id, e as verificações do plano apontam o problema em vez de impedir a leitura. Origem, destino e reservas usam só o código, então não são buscados.

**Validação de existência é regra de negócio.** A checagem de que origem, destino e aerovias existem saiu do adaptador JPA e está em `PlanoDeVooUseCase`, via `RedeAereaPort`.

## Pontos em aberto em relação ao contrato C1

- **FKs mantidas nesta etapa.** As FKs de V6/V7 para `AEROPORTO`/`AEROVIA` foram criadas sem nome, e os nomes gerados diferem entre Postgres e H2. Enquanto o adaptador local lê essas tabelas, as FKs continuam corretas. Na E3-02 (#11), `DROP TABLE AEROVIA, AEROPORTO CASCADE` remove tabelas e FKs nos dois bancos.
- **`qtdVagas` em vez de `List<Integer> vagas`.** O `malha-aerea` ainda expõe `qtdVagas`; a troca para a lista de vagas da C1 acontece quando o cadastro tiver o modelo de vagas (2 pontos de uso na gestão).
- **`listarAerovias(origem, destino)` não implementado.** Nenhum uso na gestão até agora.

## Consequências

- Na E3-02 basta trocar `RedeAereaJpaAdapter` por um adaptador HTTP; casos de uso e domínio não mudam.
- Com o adaptador HTTP, cada leitura de plano faz uma chamada por aerovia distinta do percurso.
