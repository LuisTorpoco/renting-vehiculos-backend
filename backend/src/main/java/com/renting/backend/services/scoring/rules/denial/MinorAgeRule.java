package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.Period;

@Component
public class MinorAgeRule implements DenialRule {

    private static final int LEGAL_AGE = 18;

    @Override
    public boolean evaluate(ScoringContext context) {
        if (context.getCustomer() == null || context.getCustomer().getBirthdate() == null) {
            return false;
        }

        LocalDate birthdate = context.getCustomer().getBirthdate();
        int age = Period.between(birthdate, LocalDate.now()).getYears();

        return age < LEGAL_AGE;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El cliente debe ser mayor de 18 años para realizar una solicitud de renting.";
    }
}