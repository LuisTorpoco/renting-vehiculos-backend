package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.enums.RequestStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.Rule;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CriticalRiskRule implements Rule {

    @Override
    public boolean evaluate(ScoringContext context) {

        LocalDateTime twoYearsAgo =
                LocalDateTime.now().minusYears(2);

        long deniedRequests =
                context.getCustomer()
                        .getRequests()
                        .stream()
                        .filter(request ->
                                request.getState()
                                        == RequestStatus.DENIED)
                        .filter(request ->
                                request.getResolutionDate() != null)
                        .filter(request ->
                                request.getResolutionDate()
                                        .isAfter(twoYearsAgo))
                        .count();

        return deniedRequests < 3;
    }

    @Override
    public String getMessage() {

        return "El cliente tiene demasiadas solicitudes denegadas";
    }
}