package com.renting.backend.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeRequest {

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal preTaxes;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal postTaxes;
}