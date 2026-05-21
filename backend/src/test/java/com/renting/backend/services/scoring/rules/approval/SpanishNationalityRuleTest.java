package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SpanishNationalityRule Tests")
class SpanishNationalityRuleTest {

    private SpanishNationalityRule spanishNationalityRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        spanishNationalityRule = new SpanishNationalityRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when nationality is ESPAÑOLA")
    void testEvaluateWithSpanishNationalityUpperCase() {
        customer.setNationality("ESPAÑOLA");
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when nationality is SPAIN")
    void testEvaluateWithSPAINNationality() {
        customer.setNationality("SPAIN");
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when nationality is spanish (lowercase)")
    void testEvaluateWithSpanishNationalityLowerCase() {
        customer.setNationality("española");
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when nationality is spain (lowercase)")
    void testEvaluateWithSPAINNationalityLowerCase() {
        customer.setNationality("spain");
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when nationality is foreign")
    void testEvaluateWithForeignNationality() {
        customer.setNationality("FRANCÉS");
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when nationality is null")
    void testEvaluateWithNullNationality() {
        customer.setNationality(null);
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(spanishNationalityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about additional risk verification")
    void testGetMessage() {
        String message = spanishNationalityRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("riesgo"));
    }
}
