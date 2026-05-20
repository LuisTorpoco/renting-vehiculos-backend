package com.renting.backend.entities;

import jakarta.persistence.*;
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

    @Column(nullable = false, unique = true, length = 10)
    private String nif;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "FIRST_SURNAME", nullable = false, length = 40)
    private String firstSurname;

    @Column(name = "SECOND_SURNAME", length = 40)
    private String secondSurname;

    @Column(nullable = false, length = 20)
    private String nationality;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal scoring;

    @Column(name = "EMPLOYMENT_STATUS", length = 30)
    private String employmentStatus;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "NON_PAYMENT", nullable = false)
    private Integer nonPayment;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Integer isActive;

    @Column(name = "CAREER_TIME", nullable = false)
    private LocalDate careerTime;
}