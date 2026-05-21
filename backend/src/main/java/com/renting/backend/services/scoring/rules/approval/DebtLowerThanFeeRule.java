package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DebtLowerThanFeeRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getIncomes() == null || context.getIncomes().isEmpty()) return false;

        BigDecimal netIncome = context.getIncomes().get(0).getPostTaxes();
        BigDecimal currentFee = context.getMonthlyFee();
        BigDecimal safetyThreshold = netIncome.multiply(BigDecimal.valueOf(0.40));

        return currentFee.compareTo(safetyThreshold) <= 0;
    }

    @Override
    public String getMessage() {
        return "Fallo de aprobación: La ratio financiera cuota/ingresos está fuera del rango de seguridad.";
    }
}