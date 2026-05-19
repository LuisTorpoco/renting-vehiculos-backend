package com.renting.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "EXTRA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 30)
    private String name;

    @Column(name = "PRICE", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "CATEGORY", nullable = false, length = 30)
    private String category;

    @Column(name = "PERCENTAGE", precision = 5, scale = 2) 
    private BigDecimal percentage;
}