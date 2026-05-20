package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Income;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebtLowerThanFeeRule Tests")
class DebtLowerThanFeeRuleTest {

    private DebtLowerThanFeeRule debtLowerThanFeeRule;
    private ScoringContext context;

    @BeforeEach
    void setUp() {
        debtLowerThanFeeRule = new DebtLowerThanFeeRule();
    }

    @Test
    @DisplayName("Should return true when fee is within 40% of net income")
    void testEvaluateWithValidFee() {
        Income income = new Income();
        income.setPostTaxes(BigDecimal.valueOf(2000)); // net income: 2000
        // safety threshold: 2000 * 0.40 = 800

        context = ScoringContext.builder()
                .incomes(Arrays.asList(income))
                .monthlyFee(BigDecimal.valueOf(700)) // fee < threshold
                .build();

        assertTrue(debtLowerThanFeeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when fee equals 40% of net income")
    void testEvaluateWithFeeEqualToThreshold() {
        Income income = new Income();
        income.setPostTaxes(BigDecimal.valueOf(2000));

        context = ScoringContext.builder()
                .incomes(Arrays.asList(income))
                .monthlyFee(BigDecimal.valueOf(800)) // fee = threshold
                .build();

        assertTrue(debtLowerThanFeeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when fee exceeds 40% of net income")
    void testEvaluateWithInvalidFee() {
        Income income = new Income();
        income.setPostTaxes(BigDecimal.valueOf(2000));

        context = ScoringContext.builder()
                .incomes(Arrays.asList(income))
                .monthlyFee(BigDecimal.valueOf(900)) // fee > threshold
                .build();

        assertFalse(debtLowerThanFeeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when incomes list is empty")
    void testEvaluateWithEmptyIncomesList() {
        context = ScoringContext.builder()
                .incomes(Collections.emptyList())
                .build();

        assertFalse(debtLowerThanFeeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when incomes is null")
    void testEvaluateWithNullIncomes() {
        context = ScoringContext.builder()
                .incomes(null)
                .build();

        assertFalse(debtLowerThanFeeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return error message about financial ratio")
    void testGetMessage() {
        String message = debtLowerThanFeeRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("ratio"));
    }
}
