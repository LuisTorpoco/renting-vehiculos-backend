package com.renting.backend.dtos.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerUpdateRequest {

    private String name;
    private String firstSurname;
    private String secondSurname;
    private String nationality;
    private String employmentStatus;
    private String phone;
    private BigDecimal scoring;
    private Integer nonPayment;
    private LocalDate careerTime;
}