package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class HighScoringRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        return context.getCustomer()
                .getScoring()
                .compareTo(BigDecimal.valueOf(6)) > 0;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El scoring del cliente no debe ser mayor a 6.";
    }
}
