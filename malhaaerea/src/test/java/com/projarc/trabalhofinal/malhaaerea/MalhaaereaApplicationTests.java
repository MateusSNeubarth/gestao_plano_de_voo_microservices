package com.projarc.trabalhofinal.malhaaerea;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("local")
// testes em memória para não compartilhar estado com o H2 em arquivo do perfil local
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")
class MalhaaereaApplicationTests {

    @Test
    void contextLoads() {
    }

}
