package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class MaxAgePlusMonthsRule implements DenialRule {

    private static final int MAX_AGE_PLUS_MONTHS = 80;

    @Override
    public boolean evaluate(ScoringContext context) {
        LocalDate birthdate = context.getCustomer().getBirthdate();
        Integer periodInMonths = context.getRequest().getPeriodInMonths();

        if (birthdate == null || periodInMonths == null) {
            return false;
        }

        int currentAge = Period.between(birthdate, LocalDate.now()).getYears();
        int ageAtEndOfPeriod = currentAge + (periodInMonths / 12);

        return ageAtEndOfPeriod > MAX_AGE_PLUS_MONTHS;
    }

    @Override
    public String getMessage() {
        return "Solicitud denegada: La edad del cliente más el período de renta solicitado no puede exceder 80 años.";
    }
}
