package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoDeniedLast2YearsRule Tests")
class NoDeniedLast2YearsRuleTest {

    private NoDeniedLast2YearsRule noDeniedLast2YearsRule;
    private ScoringContext context;

    @BeforeEach
    void setUp() {
        noDeniedLast2YearsRule = new NoDeniedLast2YearsRule();
    }

    @Test
    @DisplayName("Should return true when customer has no denied requests in last 2 years")
    void testEvaluateWithoutDenials() {
        context = ScoringContext.builder()
                .deniedLastTwoYears(false)
                .build();

        assertTrue(noDeniedLast2YearsRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer has denied requests in last 2 years")
    void testEvaluateWithDenials() {
        context = ScoringContext.builder()
                .deniedLastTwoYears(true)
                .build();

        assertFalse(noDeniedLast2YearsRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when deniedLastTwoYears is null")
    void testEvaluateWithNullDeniedLastTwoYears() {
        context = ScoringContext.builder()
                .deniedLastTwoYears(null)
                .build();

        assertFalse(noDeniedLast2YearsRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about recent denials")
    void testGetMessage() {
        String message = noDeniedLast2YearsRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("rechazadas"));
    }
}
