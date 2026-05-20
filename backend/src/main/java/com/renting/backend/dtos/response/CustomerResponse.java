package com.renting.backend.dtos.response;

import com.renting.backend.enums.EmploymentStatus;
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
    private EmploymentStatus employmentStatus;
    private String phone;
    private Boolean nonPayment;
    private LocalDate careerTime;
}