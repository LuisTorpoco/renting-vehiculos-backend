package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.Period;

@Component
public class FraudRiskRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        LocalDate birthDate = context.getCustomer().getBirthdate();
        if (birthDate == null) return false;

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        return age >= 18;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: Alerta de riesgo legal. El solicitante es menor de edad.";
    }
}