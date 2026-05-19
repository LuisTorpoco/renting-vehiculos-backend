package com.renting.backend.services.scoring.engine;

import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.denial.DenialRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DenialRulesEngine {

    private final List<DenialRule>
            denialRules;

    public List<RuleEvaluationResponse>
    evaluate(
            ScoringContext context
    ) {

        List<RuleEvaluationResponse>
                evaluations =
                new ArrayList<>();

        for (DenialRule rule
                : denialRules) {

            boolean passed =
                    rule.evaluate(
                            context
                    );

            RuleEvaluationResponse
                    evaluation =
                    RuleEvaluationResponse
                            .builder()
                            .ruleName(
                                    rule.getClass()
                                            .getSimpleName()
                            )
                            .passed(
                                    passed
                            )
                            .message(
                                    rule.getMessage()
                            )
                            .build();

            evaluations.add(
                    evaluation
            );

            if (passed) {

                break;
            }
        }

        return evaluations;
    }
}