package com.renting.backend.entities;

import com.renting.backend.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            unique = true,
            length = 10)
    private String nif;

    @Column(nullable = false)
    private String name;

    @Column(name = "first_surname",
            nullable = false)
    private String firstSurname;

    @Column(name =
            "second_surname")
    private String secondSurname;

    @Column(name = "nationality")
    private String nationality;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Column(nullable = false)
    private BigDecimal scoring;

    @Enumerated(EnumType.STRING)
    @Column(name =
            "employment_status")
    private EmploymentStatus
            employmentStatus;

    @Column(nullable = false)
    private String phone;

    @Column(name = "non_payment")
    private Boolean nonPayment;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "career_time")
    private LocalDate careerTime;

    @OneToMany(mappedBy =
            "customer")
    private List<Income> incomes;

    @OneToMany(mappedBy =
            "customer")
    private List<Request> requests;
}
