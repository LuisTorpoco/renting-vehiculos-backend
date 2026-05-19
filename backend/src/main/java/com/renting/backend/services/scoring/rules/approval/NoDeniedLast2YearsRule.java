package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class NoDeniedLast2YearsRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        Boolean hasDenials = context.getDeniedLastTwoYears();
        //Si no tiene registros de denegacion (false) la regla pasa bien true
        return hasDenials != null && !hasDenials;
    }

    @Override
    public String getMessage() {
        return "Fallo de aprobación: El cliente cuenta con antecedentes de solicitudes rechazadas recientemente.";
    }
}