package com.renting.backend.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CustomerResponse {

    private Long id;
    private String nif;
    private String name;
    private String firstSurname;
    private String secondSurname;
    private String nationality;
    private LocalDate birthdate;
    private BigDecimal scoring;
    private String employmentStatus;
    private String phone;
    private Integer nonPayment;
    private boolean isActive;
    private LocalDate careerTime;
}