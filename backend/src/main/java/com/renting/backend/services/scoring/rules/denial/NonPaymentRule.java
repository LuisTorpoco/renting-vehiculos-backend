package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;

@Component
public class NonPaymentRule implements DenialRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // En Oracle: 1 = Tiene impagos activos (True), 0 = Sin deudas (False).
        Integer nonPayment = context.getCustomer().getNonPayment();
        return nonPayment != null && nonPayment == 1;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El cliente cuenta con registros de impagos activos.";
    }
}
