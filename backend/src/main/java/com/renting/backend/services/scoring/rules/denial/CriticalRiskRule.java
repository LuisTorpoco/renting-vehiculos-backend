package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import com.renting.backend.enums.EmploymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CriticalRiskRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        String status = context.getCustomer().getEmploymentStatus();
        if (status != null && status.equals(EmploymentStatus.SELF_EMPLOYED.name())) {

            return context.getAveragePreTaxes().compareTo(context.getVehicleInvestment()) < 0;
        }
        return false;
    }

    @Override
    public String getMessage() {
        return "Riesgo crítico detectado: El nivel de riesgo financiero supera los límites permitidos.";
    }
}