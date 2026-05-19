package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.enums.EmploymentStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class InformaRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        if (context.getCustomer()
                .getEmploymentStatus()
                != EmploymentStatus.EMPLOYED) {

            return true;
        }

        return true;
    }

    @Override
    public String getMessage() {

        return "La empresa no cumple las condiciones de INFORMA";
    }
}