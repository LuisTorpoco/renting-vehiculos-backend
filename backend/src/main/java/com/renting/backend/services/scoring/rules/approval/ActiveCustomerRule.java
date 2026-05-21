package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class ActiveCustomerRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // En Oracle: 1 = Activo, 0 = Inactivo.
        Integer isActive = context.getCustomer().getIsActive();
        return isActive != null && isActive == 1;
    }

    @Override
    public String getMessage() {
        return "La cuenta del cliente está activa y verificada de manera positiva.";
    }
}