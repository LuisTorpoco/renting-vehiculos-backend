package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class ActiveCustomerRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // Usamos .getActive() que es el método real generado por Lombok para tu entidad Customer
        Boolean isActive = context.getCustomer().getActive();
        return isActive != null && isActive;
    }

    @Override
    public String getMessage() {
        return "Fallo de aprobación: El cliente no dispone de un expediente activo.";
    }
}