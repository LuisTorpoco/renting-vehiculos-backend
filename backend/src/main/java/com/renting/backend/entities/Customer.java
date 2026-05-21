package com.renting.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true, length = 10)
    private String nif;

    @NotNull
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(name = "FIRST_SURNAME", nullable = false, length = 40)
    private String firstSurname;

    @Column(name = "SECOND_SURNAME", length = 40)
    private String secondSurname;

    @NotNull
    @Column(nullable = false, length = 20)
    private String nationality;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthdate;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal scoring;

    @NotNull
    @Column(name = "EMPLOYMENT_STATUS", length = 30)
    private String employmentStatus;

    @NotNull
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @NotNull
    @Column(name = "NON_PAYMENT", nullable = false)
    private Integer nonPayment;

    @NotNull
    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive;

    @NotNull
    @Column(name = "CAREER_TIME", nullable = false)
    private LocalDate careerTime;
}