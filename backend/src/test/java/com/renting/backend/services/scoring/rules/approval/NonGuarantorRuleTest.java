package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NonGuarantorRule Tests")
class NonGuarantorRuleTest {

    private NonGuarantorRule nonGuarantorRule;
    private ScoringContext context;

    @BeforeEach
    void setUp() {
        nonGuarantorRule = new NonGuarantorRule();
    }

    @Test
    @DisplayName("Should always return true")
    void testEvaluateAlwaysReturnsTrue() {
        context = ScoringContext.builder().build();
        assertTrue(nonGuarantorRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about guarantor status")
    void testGetMessage() {
        String message = nonGuarantorRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("garante"));
    }
}
