package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import com.renting.backend.enums.EmploymentStatus;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class InvestmentVsIncomeRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        boolean selfEmployedValid = true;

        if (context.getCustomer().getEmploymentStatus() != null &&
                context.getCustomer().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED.name())) {

            BigDecimal grossLimit = context.getAveragePreTaxes()
                    .multiply(BigDecimal.valueOf(3));

            selfEmployedValid = context.getVehicleInvestment()
                    .compareTo(grossLimit) <= 0;
        }

        return selfEmployedValid;
    }

    @Override
    public String getMessage() {
        return "La inversión del vehículo es proporcional a los ingresos demostrados.";
    }
}