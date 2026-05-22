package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.enums.EmploymentStatus;
import org.springframework.stereotype.Component;

@Component
public class CriticalRiskRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getCustomer() == null || context.getCustomer().getEmploymentStatus() == null) return false;

        String status = context.getCustomer().getEmploymentStatus();


        if (status.equals(EmploymentStatus.SELF_EMPLOYED.name())) {
            if (context.getAveragePreTaxes() == null || context.getVehicleInvestment() == null) return false;

            return context.getAveragePreTaxes().compareTo(context.getVehicleInvestment()) < 0;
        }

        return false;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: Riesgo crítico detectado. Para perfiles autónomos, el promedio de ingresos declarados no cubre el valor de inversión del vehículo solicitado.";
    }
}