package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class NoApprovedWithWarrantiesRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {

        return !context.getApprovedWithWarranties();
    }

    @Override
    public String getMessage() {

        return "El cliente ha sido aprobado con garantías en los últimos 2 años";
    }
}