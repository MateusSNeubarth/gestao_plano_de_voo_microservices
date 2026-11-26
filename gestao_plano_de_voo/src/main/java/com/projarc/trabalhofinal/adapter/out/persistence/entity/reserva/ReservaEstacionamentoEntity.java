package com.projarc.trabalhofinal.adapter.out.persistence.entity.reserva;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Entity
@DiscriminatorValue("ESTACIONAMENTO")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReservaEstacionamentoEntity extends ReservaEntity {

    @Column(name = "codigo_aeroporto")
    private String codigoAeroporto;
    @Column(name = "numero_vaga")
    private Integer numeroVaga;
}
