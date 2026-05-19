package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.interfaces.ScoringService;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.utils.FinancialAverageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoringServiceImpl
        implements ScoringService {

    private final IncomeRepository
            incomeRepository;

    private final RequestRepository
            requestRepository;

    private final ApprovalRulesEngine
            approvalRulesEngine;

    private final DenialRulesEngine
            denialRulesEngine;

    private final FinancialAverageService
            financialAverageService;

    @Override
    public ScoringResponse evaluate(
            Customer customer,
            Request request
    ) {

        List<Income> incomes =
                incomeRepository
                        .findByCustomerIdOrderByCreatedAtDesc(
                                customer.getId()
                        );

        boolean deniedLastTwoYears =
                requestRepository
                        .countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                                customer.getId(),
                                RequestStatus.DENIED,
                                LocalDateTime.now()
                                        .minusYears(2)
                        ) > 0;

        boolean approvedWithWarranties =
                requestRepository
                        .countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                                customer.getId(),
                                RequestStatus.APPROVED_WITH_WARRANTIES,
                                LocalDateTime.now()
                                        .minusYears(2)
                        ) > 0;

        ScoringContext context =
                ScoringContext.builder()
                        .customer(customer)
                        .request(request)
                        .incomes(incomes)
                        .averagePreTaxes(
                                financialAverageService
                                        .calculateAveragePreTaxes(
                                                incomes
                                        )
                        )
                        .averagePostTaxes(
                                financialAverageService
                                        .calculateAveragePostTaxes(
                                                incomes
                                        )
                        )
                        .deniedLastTwoYears(
                                deniedLastTwoYears
                        )
                        .approvedWithWarranties(
                                approvedWithWarranties
                        )
                        .build();

        List<RuleEvaluationResponse>
                deniedRules =
                denialRulesEngine
                        .evaluate(context);

        boolean autoDenied =
                deniedRules.stream()
                        .anyMatch(
                                RuleEvaluationResponse::getPassed
                        );

        if (autoDenied) {

            return ScoringResponse
                    .builder()
                    .automaticallyApproved(false)
                    .automaticallyDenied(true)
                    .reason(
                            "Automatic denial"
                    )
                    .evaluatedRules(
                            deniedRules
                    )
                    .build();
        }

        List<RuleEvaluationResponse>
                approvedRules =
                approvalRulesEngine
                        .evaluate(context);

        boolean autoApproved =
                approvedRules.stream()
                        .allMatch(
                                RuleEvaluationResponse::getPassed
                        );

        if (autoApproved) {

            return ScoringResponse
                    .builder()
                    .automaticallyApproved(true)
                    .automaticallyDenied(false)
                    .reason(
                            "Automatic approval"
                    )
                    .evaluatedRules(
                            approvedRules
                    )
                    .build();
        }

        return ScoringResponse
                .builder()
                .automaticallyApproved(false)
                .automaticallyDenied(false)
                .reason(
                        "Pending analyst review"
                )
                .evaluatedRules(
                        approvedRules
                )
                .build();
    }
}