package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de DebtGreaterThanFeeRule")
class DebtGreaterThanFeeRuleTest {

    private DebtGreaterThanFeeRule rule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        rule = new DebtGreaterThanFeeRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Debe retornar false cuando la lista de ingresos está vacía")
    void testEvaluarSinIngresos() {
        context = ScoringContext.builder().customer(customer).incomes(Collections.emptyList()).monthlyFee(BigDecimal.valueOf(100)).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar true cuando la cuota es <= 40% de postTaxes (comportamiento actual)")
    void testEvaluarCuotaMenorOIgual40Porciento() {
        Income inc = new Income();
        inc.setPostTaxes(BigDecimal.valueOf(1000));
        context = ScoringContext.builder().customer(customer).incomes(Collections.singletonList(inc)).monthlyFee(BigDecimal.valueOf(300)).build();
        assertTrue(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar false cuando la cuota es > 40% de postTaxes")
    void testEvaluarCuotaMayor40Porciento() {
        Income inc = new Income();
        inc.setPostTaxes(BigDecimal.valueOf(1000));
        context = ScoringContext.builder().customer(customer).incomes(Collections.singletonList(inc)).monthlyFee(BigDecimal.valueOf(500)).build();
        assertFalse(rule.evaluate(context));
    }

    @Test
    @DisplayName("Debe retornar un mensaje explicativo")
    void testObtenerMensaje() {
        String msg = rule.getMessage();
        assertNotNull(msg);
        assertFalse(msg.isEmpty());
    }
}
