package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de FraudRiskRule")
class FraudRiskRuleTest {

    private FraudRiskRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new FraudRiskRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true para edad >= 18")
    void testEvaluarMayorEdad() {
        customer.setBirthdate(LocalDate.now().minusYears(30));
        context = ScoringContext.builder().customer(customer).build();
        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false para edad < 18")
    void testEvaluarMenorEdad() {
        customer.setBirthdate(LocalDate.now().minusYears(17));
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando birthdate es null")
    void testEvaluarBirthdateNull() {
        customer.setBirthdate(null);
        context = ScoringContext.builder().customer(customer).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar mensaje de fraude")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
