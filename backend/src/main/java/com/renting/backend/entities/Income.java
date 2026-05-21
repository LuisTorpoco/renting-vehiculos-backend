package com.renting.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "income")
@Getter
@Setter
public class Income {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    private Long id;

    @Column(name =
            "pre_taxes")
    @NotNull
    private BigDecimal preTaxes;

    @Column(name =
            "post_taxes")
    @NotNull
    private BigDecimal postTaxes;

    @Column(name =
            "created_at")
    private LocalDateTime
            createdAt;

    @ManyToOne(fetch =
            FetchType.LAZY)
    @JoinColumn(name =
            "customer_id")
    private Customer customer;
}