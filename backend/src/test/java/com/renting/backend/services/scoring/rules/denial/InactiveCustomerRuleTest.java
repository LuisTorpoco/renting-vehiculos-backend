package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de InactiveCustomerRule")
class InactiveCustomerRuleTest {

    private InactiveCustomerRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new InactiveCustomerRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true cuando isActive = 0")
    void testEvaluarInactivo() {
        customer.setIsActive(0);
        context = ScoringContext.builder().customer(customer).build();
        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando isActive = 1 o null")
    void testEvaluarActivoONull() {
        customer.setIsActive(1);
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));

        customer.setIsActive(null);
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar mensaje sobre perfil inactivo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
