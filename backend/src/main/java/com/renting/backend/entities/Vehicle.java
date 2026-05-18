package com.renting.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VEHICLES")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "brand", nullable = false, length = 100)
    private String brand; // Marca

    @Column(name = "model", nullable = false, length = 100)
    private String model; // Modelo

    @Column(name = "engine_displacement", length = 50)
    private String engineDisplacement; // Cilindrada

    @Column(name = "power")
    private Integer power; // Potencia en CV

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "seats")
    private Integer seats; // Número de plazas

    @Column(name = "base_investment", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseInvestment; // Inversión inicial base del coche

    @Column(name = "base_monthly_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseMonthlyFee; // Cuota mensual base calculada a 12 meses

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1; // Borrado lógico para el catálogo

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "VEHICLE_EXTRAS",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "extra_id")
    )
    private List<Extra> availableExtras;
}