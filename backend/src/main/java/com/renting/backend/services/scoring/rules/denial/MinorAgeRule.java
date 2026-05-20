package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class MinorAgeRule implements Rule {

    private static final int LEGAL_AGE = 18;

    @Override
    public boolean evaluate(ScoringContext context) {
        LocalDate birthdate = context.getCustomer().getBirthdate();

        if (birthdate == null) {
            return false;
        }

        int age = Period.between(birthdate, LocalDate.now()).getYears();
        return age < LEGAL_AGE;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: El cliente debe ser mayor de 18 años para realizar una solicitud.";
    }
}
