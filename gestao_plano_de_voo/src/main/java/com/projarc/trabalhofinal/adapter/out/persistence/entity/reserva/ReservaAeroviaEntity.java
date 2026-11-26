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
@DiscriminatorValue("AEROVIA")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReservaAeroviaEntity extends ReservaEntity {

    @Column(name = "aerovia_id")
    private String aeroviaId;
    private Integer altitude;
}
