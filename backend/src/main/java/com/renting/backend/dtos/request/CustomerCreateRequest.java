package com.renting.backend.dtos.request;

import com.renting.backend.enums.EmploymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerCreateRequest {

    @NotBlank
    private String nif;

    @NotBlank
    private String name;

    @NotBlank
    private String firstSurname;

    private String secondSurname;

    @NotBlank
    private String nationality;

    @NotNull
    private LocalDate birthdate;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal scoring;

    @NotNull
    private EmploymentStatus employmentStatus;

    @NotBlank
    private String phone;

    @NotNull
    private Boolean nonPayment;

    @NotNull
    private LocalDate careerTime;
}