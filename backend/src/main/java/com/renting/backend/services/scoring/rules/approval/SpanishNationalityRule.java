package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;

@Component
public class SpanishNationalityRule implements ApprovalRule {

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getCustomer() == null || context.getCustomer().getNationality() == null) return false;


        String nationality = context.getCustomer().getNationality().trim();

        return !nationality.equalsIgnoreCase("ES");
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El solicitante no cumple con el requisito de nacionalidad o residencia requerido por la política local.";
    }
}