-- Dev only: credenciais fixas, o banco só existe dentro do docker compose.
CREATE USER registro_aprovacoes WITH PASSWORD 'registro_aprovacoes';
CREATE DATABASE registro_aprovacoes OWNER registro_aprovacoes;
REVOKE CONNECT ON DATABASE registro_aprovacoes FROM PUBLIC;
