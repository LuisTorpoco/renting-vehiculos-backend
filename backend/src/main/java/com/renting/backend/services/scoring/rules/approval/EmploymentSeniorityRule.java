package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
public class EmploymentSeniorityRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        if (context.getCustomer()
                .getCareerTime() == null) {

            return false;
        }

        int years =
                Period.between(
                        context.getCustomer()
                                .getCareerTime(),
                        LocalDate.now()
                ).getYears();

        return years >= 3;
    }

    @Override
    public String getMessage() {

        return "El cliente debe tener al menos 3 años de antigüedad laboral";
    }
}