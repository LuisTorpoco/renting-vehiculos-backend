package com.renting.backend.services.scoring.engine;

import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.rules.approval.ApprovalRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApprovalRulesEngine {

    private final List<ApprovalRule>
            approvalRules;

    public List<RuleEvaluationResponse>
    evaluate(
            ScoringContext context
    ) {

        return approvalRules
                .stream()
                .map(rule -> {

                    boolean passed =
                            rule.evaluate(
                                    context
                            );

                    return RuleEvaluationResponse
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
                })
                .toList();
    }
}