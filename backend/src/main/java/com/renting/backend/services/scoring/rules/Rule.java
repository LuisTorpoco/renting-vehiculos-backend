package com.renting.backend.services.scoring.rules;

import com.renting.backend.services.scoring.context.ScoringContext;

public interface Rule {

    boolean evaluate(ScoringContext context);

    String getMessage();
}