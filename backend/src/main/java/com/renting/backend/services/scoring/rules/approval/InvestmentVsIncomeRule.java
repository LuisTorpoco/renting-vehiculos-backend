package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.enums.EmploymentStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvestmentVsIncomeRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        boolean postTaxesValid =
                context.getVehicleInvestment()
                        .compareTo(
                                context.getAveragePostTaxes()
                        ) <= 0;

        boolean selfEmployedValid = true;

        if (context.getCustomer()
                .getEmploymentStatus()
                == EmploymentStatus.SELF_EMPLOYED) {

            BigDecimal grossLimit =
                    context.getAveragePreTaxes()
                            .multiply(BigDecimal.valueOf(3));

            selfEmployedValid =
                    context.getVehicleInvestment()
                            .compareTo(grossLimit) <= 0;
        }

        return postTaxesValid
                && selfEmployedValid;
    }

    @Override
    public String getMessage() {

        return "La inversión supera los límites de ingresos del cliente";
    }
}