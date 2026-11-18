-- Dev only: credenciais fixas, o banco só existe dentro do docker compose.
CREATE USER gestao_planos WITH PASSWORD 'gestao_planos';
CREATE DATABASE gestao_planos OWNER gestao_planos;
REVOKE CONNECT ON DATABASE gestao_planos FROM PUBLIC;
