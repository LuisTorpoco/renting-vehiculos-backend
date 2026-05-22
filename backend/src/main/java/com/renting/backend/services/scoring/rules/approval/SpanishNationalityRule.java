package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;

@Component
public class SpanishNationalityRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        String nationality = context.getCustomer().getNationality();

        return nationality != null && (
                nationality.equalsIgnoreCase("ES")
                        || nationality.equalsIgnoreCase("ESPAÑA")
                        || nationality.equalsIgnoreCase("ESPAÑOLA")
                        || nationality.equalsIgnoreCase("SPAIN")
        );
    }

    @Override
    public String getMessage() {
        return "El cliente tiene nacionalidad española.";
    }
}