package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class FraudRiskRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getCustomer() == null || context.getCustomer().getScoring() == null) return false;


        return context.getCustomer().getScoring().compareTo(BigDecimal.valueOf(2.0)) < 0;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: Alerta de riesgo crítico de seguridad. El nivel de scoring del perfil es incompatible con las políticas de riesgo de la empresa.";
    }
}