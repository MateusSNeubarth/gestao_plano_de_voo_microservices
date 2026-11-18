# ADR 003: Ambiente reproduzível com Docker Compose

**Status:** aceita

## Contexto

O sistema precisa subir inteiro com um comando a partir de um clone limpo, com um banco por serviço e acesso externo restrito ao necessário.

## Decisão

Um único `docker-compose.yaml` na raiz sobe tudo: `docker compose up --build`.

| Serviço | Porta interna | Publicada | Motivo |
|---|---|---|---|
| gateway | 8765 | **8765** | Ponto de entrada único do cliente |
| nameserver (Eureka) | 8761 | **8761** | Painel para ver os serviços registrados |
| rabbitmq | 5672 / 15672 | **15672** | Só o painel de administração; o AMQP fica interno |
| gestao | 8080 | não | Acessado só pelo gateway (`/gestao/**`) |
| malhaaerea | 8081 | não | Acessado só pelo gateway (`/malha/**`) |
| registroaprovacoes | 8082 | não | Acessado só pelo gateway (`/aprovacoes/**`) |
| postgres | 5432 | não | Usado só pelos serviços |

**Banco por serviço:** um único container Postgres com um banco e um usuário por serviço (`malha_aerea`, `gestao_planos`, `registro_aprovacoes`), criados por `postgres/init/*.sql`. Cada serviço recebe apenas a credencial do próprio banco.

**Sem `.env` e sem banco externo:** o projeto sempre roda com o Postgres do compose, então as credenciais são fixas e de desenvolvimento, versionadas no compose e nos scripts de init. Nenhuma credencial real existe no repositório.

**Perfis:** `prod` (usado no compose) lê `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` do ambiente; `local` usa H2 em arquivo (`./data/<servico>`) para rodar fora do Docker. Os testes usam H2 em memória.

**Ordem de inicialização:** `depends_on` com `condition: service_healthy`; os serviços esperam Eureka e Postgres, e o `registroaprovacoes` também o RabbitMQ.

## Acessos diretos possíveis

- Painéis: Eureka em `localhost:8761`, RabbitMQ em `localhost:15672` (`guest`/`guest`).
- Serviço de negócio ou banco sem passar pelo gateway: `docker compose exec <servico> curl localhost:<porta>/...` ou `docker compose exec postgres psql -U <usuario> -d <banco>`.

## Dados

- `docker compose down` + `up` preserva os dados (volume `postgres-data`).
- `docker compose down -v` apaga o volume e recomeça do zero.
- Os scripts de `postgres/init/` só rodam com o volume vazio: depois de alterá-los, é preciso `down -v`.

## Consequências

- Subir o ambiente não exige configuração prévia.
- Mudanças nos bancos/usuários exigem recriar o volume, perdendo os dados locais.
