package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MaxInvestmentRule Tests")
class MaxInvestmentRuleTest {

    private MaxInvestmentRule maxInvestmentRule;
    private ScoringContext context;

    @BeforeEach
    void setUp() {
        maxInvestmentRule = new MaxInvestmentRule();
    }

    @Test
    @DisplayName("Should return true when vehicle price is within limit (80000)")
    void testEvaluateWithValidPrice() {
        context = ScoringContext.builder()
                .vehicleInvestment(BigDecimal.valueOf(75000.00))
                .build();

        assertTrue(maxInvestmentRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when vehicle price equals the limit (80000)")
    void testEvaluateWithPriceEqualToLimit() {
        context = ScoringContext.builder()
                .vehicleInvestment(BigDecimal.valueOf(80000.00))
                .build();

        assertTrue(maxInvestmentRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when vehicle price exceeds limit (80000)")
    void testEvaluateWithExcessivePrice() {
        context = ScoringContext.builder()
                .vehicleInvestment(BigDecimal.valueOf(85000.00))
                .build();

        assertFalse(maxInvestmentRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when vehicle price is significantly below limit")
    void testEvaluateWithLowPrice() {
        context = ScoringContext.builder()
                .vehicleInvestment(BigDecimal.valueOf(20000.00))
                .build();

        assertTrue(maxInvestmentRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when vehicleInvestment is null")
    void testEvaluateWithNullVehicleInvestment() {
        context = ScoringContext.builder()
                .vehicleInvestment(null)
                .build();

        assertFalse(maxInvestmentRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about vehicle acquisition limit")
    void testGetMessage() {
        String message = maxInvestmentRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("valor"));
    }
}
