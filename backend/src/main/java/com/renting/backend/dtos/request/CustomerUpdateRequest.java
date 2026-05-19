package com.renting.backend.dtos.request;

import com.renting.backend.enums.EmploymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerUpdateRequest {

    private String name;
    private String firstSurname;
    private String secondSurname;
    private String nationality;
    private EmploymentStatus employmentStatus;
    private String phone;
    private BigDecimal scoring;
    private Boolean nonPayment;
    private LocalDate careerTime;
}