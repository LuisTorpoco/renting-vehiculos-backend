package com.renting.backend.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerRequest {

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
    @Past(message = "Birthday must be in the past")
    private LocalDate birthdate;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal scoring;

    @NotBlank
    private String employmentStatus;

    @NotBlank
    private String phone;

    @NotNull
    private Integer nonPayment;

    @NotNull
    private LocalDate careerTime;
}