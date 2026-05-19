package com.renting.backend.services.scoring.context;

import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.entities.Request;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ScoringContext
{

    private Customer customer;

    private Request request;

    private List<Income> incomes;

    private BigDecimal
            averagePreTaxes;

    private BigDecimal
            averagePostTaxes;

    private boolean
            deniedLastTwoYears;

    private boolean
            approvedWithWarranties;
}
