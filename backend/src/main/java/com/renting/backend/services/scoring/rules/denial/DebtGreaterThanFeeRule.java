package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DebtGreaterThanFeeRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getIncomes() == null || context.getIncomes().isEmpty()) return false;

        // Usamos postTaxes del primer ingreso y la cuota directa que ya viene calculada en el contexto
        BigDecimal netIncome = context.getIncomes().get(0).getPostTaxes();
        BigDecimal currentFee = context.getMonthlyFee(); 

        if (netIncome == null || currentFee == null) return false;

        BigDecimal maxAllowedDebt = netIncome.multiply(BigDecimal.valueOf(0.40));

        // Comportamiento esperado por tests: true si currentFee <= maxAllowedDebt
        return currentFee.compareTo(maxAllowedDebt) <= 0;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: La cuota mensual supera el umbral máximo de endeudamiento permitido (40%).";
    }
}