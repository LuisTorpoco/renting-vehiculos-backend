package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApprovalRule Interface Tests")
class ApprovalRuleTest {

    private static class ConcreteApprovalRule implements ApprovalRule {
        @Override
        public boolean evaluate(ScoringContext context) {
            return true;
        }

        @Override
        public String getMessage() {
            return "Test message";
        }
    }

    @Test
    @DisplayName("Should be able to instantiate ApprovalRule implementations")
    void testApprovalRuleImplementation() {
        ConcreteApprovalRule rule = new ConcreteApprovalRule();
        assertNotNull(rule);
        assertTrue(rule instanceof ApprovalRule);
        assertTrue(rule.evaluate(null));
        assertEquals("Test message", rule.getMessage());
    }
}
