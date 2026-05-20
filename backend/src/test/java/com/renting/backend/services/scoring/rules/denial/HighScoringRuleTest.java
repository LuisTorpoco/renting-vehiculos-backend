package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de HighScoringRule")
class HighScoringRuleTest {

    private HighScoringRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new HighScoringRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true cuando scoring < 6")
    void testEvaluarScoringBajo() {
        customer.setScoring(BigDecimal.valueOf(5));
        context = ScoringContext.builder().customer(customer).build();
        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando scoring >= 6")
    void testEvaluarScoringAlto() {
        customer.setScoring(BigDecimal.valueOf(6));
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar mensaje sobre scoring")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
