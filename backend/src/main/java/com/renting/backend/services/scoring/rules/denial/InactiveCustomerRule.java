package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class InactiveCustomerRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // En Oracle: 0 significa que la cuenta está inactiva/bloqueada.
        Integer isActive = context.getCustomer().getIsActive();
        return isActive != null && isActive == 0;
    }

    @Override
    public String getMessage() {
        return "El perfil del cliente se encuentra inactivo en el sistema.";
    }
}