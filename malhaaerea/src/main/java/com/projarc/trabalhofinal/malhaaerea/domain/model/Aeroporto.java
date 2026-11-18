package com.projarc.trabalhofinal.malhaaerea.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Aeroporto {

    private String codigo;
    private String nome;
    private Integer qtdVagas;
}
