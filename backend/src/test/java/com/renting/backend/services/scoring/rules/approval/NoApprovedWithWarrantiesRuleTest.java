package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NoApprovedWithWarrantiesRule Tests")
class NoApprovedWithWarrantiesRuleTest {

    private NoApprovedWithWarrantiesRule noApprovedWithWarrantiesRule;
    private ScoringContext context;

    @BeforeEach
    void setUp() {
        noApprovedWithWarrantiesRule = new NoApprovedWithWarrantiesRule();
    }

    @Test
    @DisplayName("Should return true when customer was not approved with warranties")
    void testEvaluateWithoutWarranties() {
        context = ScoringContext.builder()
                .approvedWithWarranties(false)
                .build();

        assertTrue(noApprovedWithWarrantiesRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer was approved with warranties")
    void testEvaluateWithWarranties() {
        context = ScoringContext.builder()
                .approvedWithWarranties(true)
                .build();

        assertFalse(noApprovedWithWarrantiesRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about warranty approval")
    void testGetMessage() {
        String message = noApprovedWithWarrantiesRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("garantías"));
    }
}
