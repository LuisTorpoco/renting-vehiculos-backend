package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class SpanishNationalityRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        String nationality = context.getCustomer().getNationality();
        return nationality != null && (nationality.equalsIgnoreCase("ESPAÑOLA") || nationality.equalsIgnoreCase("SPAIN"));
    }

    @Override
    public String getMessage() {
        return "Aviso de aprobación: Requiere verificación de riesgo adicional por nacionalidad extranjera.";
    }
}