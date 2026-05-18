package com.renting.backend.entities;

import com.renting.backend.enums.ExtraType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EXTRAS")
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "extra_type", nullable = false, length = 20)
    private ExtraType extraType; //Almacena 'FIXED' o 'PERCENTAGE'

    @Column(name = "value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value; //Guarda el precio fijo (ej: 500.00) o el porcentaje

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1; //Borrado lógico como pide el enunciado (1 = Activo, 0 = Borrado)
}