package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de MaxAgePlusMonthsRule")
class MaxAgePlusMonthsRuleTest {

    private MaxAgePlusMonthsRule rule;
    private ScoringContext context;
    private Customer customer;
    private Request request;

    @BeforeEach
    void setUp() {
        rule = new MaxAgePlusMonthsRule();
        customer = new Customer();
        request = new Request();
    }

    @Test
    @DisplayName("Debe retornar true cuando edad + meses supera 80 años")
    void testEvaluarConEdadPlusMonthsMayorA80() {
        LocalDate birthdate = LocalDate.now().minusYears(75);
        customer.setBirthdate(birthdate);
        request.setPeriodInMonths(72);
        context = ScoringContext.builder()
                .customer(customer)
                .request(request)
                .build();

        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando edad + meses es exactamente 80 años")
    void testEvaluarConEdadPlusMonthsIgualA80() {
        LocalDate birthdate = LocalDate.now().minusYears(75);
        customer.setBirthdate(birthdate);
        request.setPeriodInMonths(60);
        context = ScoringContext.builder()
                .customer(customer)
                .request(request)
                .build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando edad + meses es menor a 80 años")
    void testEvaluarConEdadPlusMonthsMenorA80() {
        LocalDate birthdate = LocalDate.now().minusYears(50);
        customer.setBirthdate(birthdate);
        request.setPeriodInMonths(36);
        context = ScoringContext.builder()
                .customer(customer)
                .request(request)
                .build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando birthdate es null")
    void testEvaluarConBirthdateNull() {
        customer.setBirthdate(null);
        request.setPeriodInMonths(24);
        context = ScoringContext.builder()
                .customer(customer)
                .request(request)
                .build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando periodInMonths es null")
    void testEvaluarConPeriodInMonthsNull() {
        LocalDate birthdate = LocalDate.now().minusYears(50);
        customer.setBirthdate(birthdate);
        request.setPeriodInMonths(null);
        context = ScoringContext.builder()
                .customer(customer)
                .request(request)
                .build();

        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar un mensaje informativo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
        assertTrue(msg.contains("80"));
    }
}
