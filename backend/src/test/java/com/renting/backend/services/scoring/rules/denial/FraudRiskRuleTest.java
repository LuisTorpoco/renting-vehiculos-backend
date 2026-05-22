package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de FraudRiskRule")
class FraudRiskRuleTest {

    private FraudRiskRule rule;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new FraudRiskRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar true si el scoring es inferior a 2.0 (Riesgo Crítico / Denegar)")
    void testEvaluarScoringBajoRiesgo() {
        customer.setScoring(BigDecimal.valueOf(1.5)); // 1.5 < 2.0 -> Dispara la denegación
        ScoringContext context = ScoringContext.builder().customer(customer).build();

        assertTrue(rule.evaluate(context), "Debería denegar la solicitud por scoring críticamente bajo");
    }

    @Test
    @DisplayName("Debe retornar false si el scoring es seguro (Igual o mayor a 2.0)")
    void testEvaluarScoringSeguro() {
        customer.setScoring(BigDecimal.valueOf(5.0)); // 5.0 >= 2.0 -> Perfil apto, no deniega
        ScoringContext context = ScoringContext.builder().customer(customer).build();

        assertFalse(rule.evaluate(context), "No debería denegar si el cliente tiene un scoring sano");
    }

    @Test
    @DisplayName("Debe retornar false cuando el scoring es null")
    void testEvaluarScoringNull() {
        customer.setScoring(null);
        ScoringContext context = ScoringContext.builder().customer(customer).build();

        assertFalse(rule.evaluate(context), "Debería ignorar la regla si no hay datos de scoring disponibles");
    }

    @Test
    @DisplayName("Debe retornar el mensaje de alerta de riesgo crítico")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("riesgo crítico de seguridad"), "El mensaje debe alertar sobre las políticas de riesgo");
    }
}