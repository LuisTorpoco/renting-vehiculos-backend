package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.enums.EmploymentStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InformaRule Tests")
class InformaRuleTest {

    private InformaRule informaRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        informaRule = new InformaRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when customer is employed and scoring > 5")
    void testEvaluateWithEmployedAndHighScoring() {
        customer.setEmploymentStatus(EmploymentStatus.EMPLOYED.name());
        customer.setScoring(BigDecimal.valueOf(6.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(informaRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer is employed and scoring <= 5")
    void testEvaluateWithEmployedAndLowScoring() {
        customer.setEmploymentStatus(EmploymentStatus.EMPLOYED.name());
        customer.setScoring(BigDecimal.valueOf(4.5));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(informaRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer is employed and scoring equals 5")
    void testEvaluateWithEmployedAndScoringEqualsFive() {
        customer.setEmploymentStatus(EmploymentStatus.EMPLOYED.name());
        customer.setScoring(BigDecimal.valueOf(5.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(informaRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when customer is self-employed regardless of scoring")
    void testEvaluateWithSelfEmployedReturnsTrue() {
        customer.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED.name());
        customer.setScoring(BigDecimal.valueOf(3.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(informaRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when employment status is null")
    void testEvaluateWithNullEmploymentStatus() {
        customer.setEmploymentStatus(null);
        customer.setScoring(BigDecimal.valueOf(3.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(informaRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about Informa validation")
    void testGetMessage() {
        String message = informaRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("Informa"));
    }
}
