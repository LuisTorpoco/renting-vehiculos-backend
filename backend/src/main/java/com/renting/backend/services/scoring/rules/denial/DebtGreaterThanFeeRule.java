package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebtGreaterThanFeeRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getIncomes() == null || context.getIncomes().isEmpty()) {
            return false;
        }

        if (context.getMonthlyFee() == null) {
            return false;
        }

        BigDecimal netIncome = context.getIncomes().get(0).getPostTaxes();

        if (netIncome == null) {
            return false;
        }

        BigDecimal currentFee = context.getMonthlyFee();
        BigDecimal maxAllowedDebt = netIncome.multiply(BigDecimal.valueOf(0.40));

        return currentFee.compareTo(maxAllowedDebt) > 0;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: La cuota mensual supera el umbral máximo de endeudamiento permitido (40%).";
    }
}