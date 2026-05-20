package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.enums.EmploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de CriticalRiskRule")
class CriticalRiskRuleTest {

    private CriticalRiskRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new CriticalRiskRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true para SELF_EMPLOYED cuando averagePreTaxes < vehicleInvestment")
    void testEvaluarRiesgoAutonomo() {
        customer.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED.name());
        context = ScoringContext.builder().customer(customer).averagePreTaxes(BigDecimal.valueOf(1000)).vehicleInvestment(BigDecimal.valueOf(2000)).build();
        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando no es self-employed")
    void testEvaluarNoAutonomo() {
        customer.setEmploymentStatus(EmploymentStatus.EMPLOYED.name());
        context = ScoringContext.builder().customer(customer).averagePreTaxes(BigDecimal.valueOf(1000)).vehicleInvestment(BigDecimal.valueOf(2000)).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar un mensaje de riesgo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
