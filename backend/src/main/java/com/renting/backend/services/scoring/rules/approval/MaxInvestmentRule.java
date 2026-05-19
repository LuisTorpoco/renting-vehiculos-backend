package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MaxInvestmentRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // Usamos el atributo exacto del contexto de tu compañero
        BigDecimal vehiclePrice = context.getVehicleInvestment();
        return vehiclePrice != null && vehiclePrice.compareTo(BigDecimal.valueOf(80000.00)) <= 0;
    }

    @Override
    public String getMessage() {
        return "Fallo de aprobación: El valor de adquisición del vehículo supera el límite estándar de la compañía.";
    }
}