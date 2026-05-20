package com.renting.backend.ScoringTesting;

import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.IncomeRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.impl.ScoringServiceImpl;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.utils.FinancialAverageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ScoringServiceImpl")
class ScoringServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ApprovalRulesEngine approvalRulesEngine;

    @Mock
    private DenialRulesEngine denialRulesEngine;

    @Mock
    private FinancialAverageService financialAverageService;

    @InjectMocks
    private ScoringServiceImpl scoringService;

    private Customer customer;
    private Request request;
    private List<Income> incomes;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .build();

        request = Request.builder()
                .id(100L)
                .customer(customer)
                .build();

        incomes = Collections.emptyList();

        // Default: nada en los repos, ningún antecedentes.
        when(incomeRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()))
                .thenReturn(incomes);

        when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                eq(customer.getId()),
                eq(RequestStatus.DENIED),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                eq(customer.getId()),
                eq(RequestStatus.APPROVED_WITH_WARRANTIES),
                any(LocalDateTime.class)))
                .thenReturn(0L);

        when(financialAverageService.calculateAveragePreTaxes(incomes))
                .thenReturn(new BigDecimal("3000.00"));
        when(financialAverageService.calculateAveragePostTaxes(incomes))
                .thenReturn(new BigDecimal("2400.00"));
    }

    // ---------------------------------------------------------------------
    // Camino 1: denegación automática
    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("cuando alguna regla de denegación pasa")
    class WhenDenied {

        @Test
        @DisplayName("devuelve denegación automática con las reglas evaluadas")
        void returnsAutomaticDenial() {
            RuleEvaluationResponse passingDenial = ruleResult("FraudRiskRule", true, "denegado");
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(passingDenial));

            ScoringResponse result = scoringService.evaluate(customer, request);

            assertThat(result.getAutomaticallyApproved()).isFalse();
            assertThat(result.getAutomaticallyDenied()).isTrue();
            assertThat(result.getReason()).isEqualTo("Automatic denial");
            assertThat(result.getEvaluatedRules()).containsExactly(passingDenial);
        }

        @Test
        @DisplayName("hace short-circuit: NO evalúa las reglas de aprobación")
        void shortCircuitsApprovalEngine() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("FraudRiskRule", true, "denegado")));

            scoringService.evaluate(customer, request);

            verify(approvalRulesEngine, never()).evaluate(any());
        }

        @Test
        @DisplayName("basta con que UNA regla de denegación pase entre varias")
        void anyMatchingDenialRuleTriggersDenial() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(
                            ruleResult("A", false, "no aplica"),
                            ruleResult("B", true, "aplica"),
                            ruleResult("C", false, "no aplica")
                    ));

            ScoringResponse result = scoringService.evaluate(customer, request);

            assertThat(result.getAutomaticallyDenied()).isTrue();
        }
    }

    // ---------------------------------------------------------------------
    // Camino 2: aprobación automática
    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("cuando todas las reglas de aprobación pasan")
    class WhenApproved {

        @Test
        @DisplayName("devuelve aprobación automática con las reglas evaluadas")
        void returnsAutomaticApproval() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));

            RuleEvaluationResponse a1 = ruleResult("RuleA", true, "ok");
            RuleEvaluationResponse a2 = ruleResult("RuleB", true, "ok");
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(a1, a2));

            ScoringResponse result = scoringService.evaluate(customer, request);

            assertThat(result.getAutomaticallyApproved()).isTrue();
            assertThat(result.getAutomaticallyDenied()).isFalse();
            assertThat(result.getReason()).isEqualTo("Automatic approval");
            assertThat(result.getEvaluatedRules()).containsExactly(a1, a2);
        }

        @Test
        @DisplayName("evalúa siempre primero denegación y después aprobación")
        void evaluatesDenialBeforeApproval() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("Y", true, "ok")));

            scoringService.evaluate(customer, request);

            var inOrder = org.mockito.Mockito.inOrder(denialRulesEngine, approvalRulesEngine);
            inOrder.verify(denialRulesEngine).evaluate(any(ScoringContext.class));
            inOrder.verify(approvalRulesEngine).evaluate(any(ScoringContext.class));
        }
    }

    // ---------------------------------------------------------------------
    // Camino 3: pendiente de revisión
    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("cuando ni denegación ni aprobación total")
    class WhenPending {

        @Test
        @DisplayName("devuelve pendiente de revisión por analista")
        void returnsPendingAnalystReview() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));

            RuleEvaluationResponse a1 = ruleResult("RuleA", true, "ok");
            RuleEvaluationResponse a2 = ruleResult("RuleB", false, "no cumple");
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(a1, a2));

            ScoringResponse result = scoringService.evaluate(customer, request);

            assertThat(result.getAutomaticallyApproved()).isFalse();
            assertThat(result.getAutomaticallyDenied()).isFalse();
            assertThat(result.getReason()).isEqualTo("Pending analyst review");
            assertThat(result.getEvaluatedRules()).containsExactly(a1, a2);
        }

        @Test
        @DisplayName("basta con que UNA approval rule falle para no auto-aprobar")
        void singleFailedApprovalBreaksAutoApproval() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(
                            ruleResult("A", true, "ok"),
                            ruleResult("B", true, "ok"),
                            ruleResult("C", false, "falla")
                    ));

            ScoringResponse result = scoringService.evaluate(customer, request);

            assertThat(result.getAutomaticallyApproved()).isFalse();
            assertThat(result.getAutomaticallyDenied()).isFalse();
            assertThat(result.getReason()).isEqualTo("Pending analyst review");
        }
    }

    // ---------------------------------------------------------------------
    // Construcción del ScoringContext
    // ---------------------------------------------------------------------
    @Nested
    @DisplayName("construcción del ScoringContext")
    class ContextBuilding {

        @Test
        @DisplayName("propaga customer, request, incomes y medias al contexto")
        void buildsContextWithAllData() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("Y", true, "ok")));

            scoringService.evaluate(customer, request);

            ScoringContext ctx = captureDenialContext();


            assertThat(ctx.getCustomer()).isSameAs(customer);
            assertThat(ctx.getRequest()).isSameAs(request);
            assertThat(ctx.getIncomes()).isSameAs(incomes);
            assertThat(ctx.getAveragePreTaxes()).isEqualByComparingTo("3000.00");
            assertThat(ctx.getAveragePostTaxes()).isEqualByComparingTo("2400.00");
            assertThat(ctx.getDeniedLastTwoYears()).isFalse();
            assertThat(ctx.getApprovedWithWarranties()).isFalse();
        }

        @Test
        @DisplayName("marca deniedLastTwoYears=true cuando hay denegaciones previas")
        void flagsDeniedLastTwoYearsWhenPresent() {
            when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                    eq(customer.getId()),
                    eq(RequestStatus.DENIED),
                    any(LocalDateTime.class)))
                    .thenReturn(2L);
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("Y", true, "ok")));

            scoringService.evaluate(customer, request);

            assertThat(captureDenialContext().getDeniedLastTwoYears()).isTrue();
        }

        @Test
        @DisplayName("marca approvedWithWarranties=true cuando hay antecedente")
        void flagsApprovedWithWarrantiesWhenPresent() {
            when(requestRepository.countByCustomerIdAndStateAndCreatedAtGreaterThanEqual(
                    eq(customer.getId()),
                    eq(RequestStatus.APPROVED_WITH_WARRANTIES),
                    any(LocalDateTime.class)))
                    .thenReturn(1L);
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", false, "no deniega")));
            when(approvalRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("Y", true, "ok")));

            scoringService.evaluate(customer, request);

            assertThat(captureDenialContext().getApprovedWithWarranties()).isTrue();
        }

        @Test
        @DisplayName("consulta los ingresos del customer correcto")
        void queriesIncomesForCustomer() {
            when(denialRulesEngine.evaluate(any(ScoringContext.class)))
                    .thenReturn(List.of(ruleResult("X", true, "deniega")));

            scoringService.evaluate(customer, request);

            verify(incomeRepository).findByCustomerIdOrderByCreatedAtDesc(customer.getId());
        }

        private ScoringContext captureDenialContext() {
            ArgumentCaptor<ScoringContext> captor = ArgumentCaptor.forClass(ScoringContext.class);
            verify(denialRulesEngine).evaluate(captor.capture());
            return captor.getValue();
        }
    }

    // ---------------------------------------------------------------------
    // Helper para funcionamiento
    // ---------------------------------------------------------------------
    private static RuleEvaluationResponse ruleResult(String name, boolean passed, String message) {
        return RuleEvaluationResponse.builder()
                .ruleName(name)
                .passed(passed)
                .message(message)
                .build();
    }
}
