package com.renting.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "VEHICLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LICENSE_PLATE", unique = true, nullable = false, length = 10)
    private String licensePlate;

    @Column(name = "BRAND", nullable = false, length = 30)
    private String brand;

    @Column(name = "MODEL", nullable = false, length = 30)
    private String model;

    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Reemplaza a baseInvestment

    @Column(name = "CC", nullable = false)
    private Integer cc;

    @Column(name = "POTENCY", nullable = false)
    private Integer potency;

    @Column(name = "COLOR", nullable = false, length = 20)
    private String color;

    @Column(name = "SPOTS", nullable = false)
    private Integer spots;

    @Column(name = "BASE_MONTHLY_FEE", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseMonthlyFee;

    @Column(name = "AVAILABLE", nullable = false)
    private Integer available; // 1 = Disponible, 0 = No disponible (Reemplaza a isActive)
}