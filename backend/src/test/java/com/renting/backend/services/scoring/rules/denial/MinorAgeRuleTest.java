package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MinorAgeRule")
class MinorAgeRuleTest {

    private MinorAgeRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new MinorAgeRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true cuando el cliente es menor de 18 años")
    void testEvaluarConClienteMenorDeEdad() {
        LocalDate birthdate = LocalDate.now().minusYears(17);
        customer.setBirthdate(birthdate);
        context = ScoringContext.builder().customer(customer).build();

        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando el cliente tiene exactamente 18 años")
    void testEvaluarConClienteDe18Años() {
        LocalDate birthdate = LocalDate.now().minusYears(18);
        customer.setBirthdate(birthdate);
        context = ScoringContext.builder().customer(customer).build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando el cliente es mayor de 18 años")
    void testEvaluarConClienteMayorDeEdad() {
        LocalDate birthdate = LocalDate.now().minusYears(25);
        customer.setBirthdate(birthdate);
        context = ScoringContext.builder().customer(customer).build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando el birthdate es null")
    void testEvaluarConBirthdateNull() {
        customer.setBirthdate(null);
        context = ScoringContext.builder().customer(customer).build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar un mensaje informativo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
        assertTrue(msg.contains("18"));
    }
}
