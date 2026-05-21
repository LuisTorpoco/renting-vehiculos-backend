package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de NonPaymentRule")
class NonPaymentRuleTest {

    private NonPaymentRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new NonPaymentRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true cuando nonPayment = 1")
    void testEvaluarConImpago() {
        customer.setNonPayment(1);
        context = ScoringContext.builder().customer(customer).build();

        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando nonPayment = 0 o null")
    void testEvaluarSinImpago() {
        customer.setNonPayment(0);
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));

        customer.setNonPayment(null);
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar un mensaje informativo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
