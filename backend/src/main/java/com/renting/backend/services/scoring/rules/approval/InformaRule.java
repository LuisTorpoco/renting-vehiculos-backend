package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.enums.EmploymentStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InformaRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        String status = context.getCustomer().getEmploymentStatus();

        if (status != null && status.equals(EmploymentStatus.EMPLOYED.name())) {
            return context.getCustomer().getScoring() != null
                    && context.getCustomer().getScoring().compareTo(BigDecimal.valueOf(5)) < 0;
        }

        return true;
    }

    @Override
    public String getMessage() {
        return "Validación de Informa completada correctamente.";
    }
}