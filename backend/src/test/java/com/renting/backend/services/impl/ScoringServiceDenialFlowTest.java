package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.utils.FinancialAverageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ScoringServiceImpl Denial Flow Tests")
class ScoringServiceDenialFlowTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private DenialRulesEngine denialRulesEngine;

    @Mock
    private FinancialAverageService financialAverageService;

    @InjectMocks
    private ScoringServiceImpl scoringService;

    private Customer customer;
    private Request request;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        request = new Request();
        request.setId(1L);
    }

    @Test
    @DisplayName("Should auto-deny when denial engine returns a passed rule")
    void testAutoDenialFlow() {
        when(incomeRepository.findByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(eq(1L), eq(RequestStatus.DENIED), any(LocalDateTime.class))).thenReturn(0L);
        when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(eq(1L), eq(RequestStatus.APPROVED_WITH_WARRANTIES), any(LocalDateTime.class))).thenReturn(0L);
        when(financialAverageService.calculateAveragePreTaxes(Collections.emptyList())).thenReturn(BigDecimal.ZERO);
        when(financialAverageService.calculateAveragePostTaxes(Collections.emptyList())).thenReturn(BigDecimal.ZERO);

        RuleEvaluationResponse resp = new RuleEvaluationResponse();
        resp.setPassed(true);
        resp.setRuleName("NonPaymentRule");
        resp.setMessage("Denegado por impagos");

        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.singletonList(resp));

        ScoringResponse result = scoringService.evaluate(customer, request);

        assertNotNull(result);
        assertTrue(result.getAutomaticallyDenied());
        assertFalse(result.getAutomaticallyApproved());
        assertEquals("Automatic denial", result.getReason());
        assertNotNull(result.getEvaluatedRules());
        assertEquals(1, result.getEvaluatedRules().size());
        assertEquals("NonPaymentRule", result.getEvaluatedRules().get(0).getRuleName());
    }
}
