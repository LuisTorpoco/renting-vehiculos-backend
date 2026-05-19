package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class InactiveCustomerRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        Boolean isActive = context.getCustomer().getActive();
        return isActive != null && isActive;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El expediente del cliente se encuentra inactivo.";
    }
}