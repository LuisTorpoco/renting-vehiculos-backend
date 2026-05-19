package com.renting.backend.services.scoring.utils;

import com.renting.backend.entities.Income;

import java.math.BigDecimal;
import java.util.List;

public interface FinancialAverageService {

    BigDecimal calculateAveragePreTaxes(
            List<Income> incomes
    );

    BigDecimal calculateAveragePostTaxes(
            List<Income> incomes
    );
}