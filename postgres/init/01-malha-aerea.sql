-- Dev only: credenciais fixas, o banco só existe dentro do docker compose.
CREATE USER malha_aerea WITH PASSWORD 'malha_aerea';
CREATE DATABASE malha_aerea OWNER malha_aerea;
REVOKE CONNECT ON DATABASE malha_aerea FROM PUBLIC;
