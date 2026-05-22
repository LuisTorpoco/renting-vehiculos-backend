package com.renting.backend.services.scoring.rules.denial;

import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.entities.Income;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DebtGreaterThanFeeRuleTest {

    private DebtGreaterThanFeeRule rule;

    @BeforeEach
    void setUp() {
        rule = new DebtGreaterThanFeeRule();
    }

    @Test
    void testEvaluarCuotaMayor40Porciento() {
        // Creamos el ingreso simulado
        Income income = new Income();
        income.setPostTaxes(BigDecimal.valueOf(1000));


        ScoringContext context = ScoringContext.builder()
                .incomes(Collections.singletonList(income))
                .monthlyFee(BigDecimal.valueOf(500))
                .build();

        assertTrue(rule.evaluate(context), "Debería denegar porque la cuota supera el 40% de los ingresos");
    }

    @Test
    void testEvaluarCuotaMenorOIgual40Porciento() {

        Income income = new Income();
        income.setPostTaxes(BigDecimal.valueOf(1000)); // El 40% son 400

        ScoringContext context = ScoringContext.builder()
                .incomes(Collections.singletonList(income))
                .monthlyFee(BigDecimal.valueOf(300)) // 300 <= 400 -> No debe denegar (false)
                .build();

        assertFalse(rule.evaluate(context), "No debería denegar porque la cuota es inferior al umbral del 40%");
    }
}