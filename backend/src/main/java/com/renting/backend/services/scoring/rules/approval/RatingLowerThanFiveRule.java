package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RatingLowerThanFiveRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        return context.getCustomer()
                .getScoring()
                .compareTo(BigDecimal.valueOf(5)) < 0;
    }

    @Override
    public String getMessage() {

        return "El rating del cliente debe ser inferior a 5";
    }
}