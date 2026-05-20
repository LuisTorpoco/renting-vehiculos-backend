package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import com.renting.backend.enums.EmploymentStatus;
import org.springframework.stereotype.Component;

@Component
public class InformaRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {
        // Corregido: Se adapta la validación de estado laboral a tipo String
        String status = context.getCustomer().getEmploymentStatus();
        if (status != null && status.equals(EmploymentStatus.EMPLOYED.name())) {
            return context.getCustomer().getScoring().doubleValue() > 5.0;
        }
        return true;
    }

    @Override
    public String getMessage() {
        return "Validación de Informa completada con éxito para el tipo de empleo actual.";
    }
}