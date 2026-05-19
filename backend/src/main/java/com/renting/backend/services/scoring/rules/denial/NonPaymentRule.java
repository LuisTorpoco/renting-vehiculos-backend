package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class NonPaymentRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        Boolean hasNonPayment = context.getCustomer().getNonPayment();
        // Si es nulo o es true, significa que tiene impagos devuelve false falla
        return hasNonPayment != null && !hasNonPayment;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El cliente figura en el registro de impagos (morosidad).";
    }
}