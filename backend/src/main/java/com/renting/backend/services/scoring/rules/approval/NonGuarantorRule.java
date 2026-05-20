package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

@Component
public class NonGuarantorRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {

        return true;
    }

    @Override
    public String getMessage() {

        return "El cliente es garante en otra operación";
    }
}