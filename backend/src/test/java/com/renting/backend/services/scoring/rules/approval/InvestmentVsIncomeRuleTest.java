package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.enums.EmploymentStatus;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvestmentVsIncomeRule Tests")
class InvestmentVsIncomeRuleTest {

    private InvestmentVsIncomeRule investmentVsIncomeRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        investmentVsIncomeRule = new InvestmentVsIncomeRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when customer is employed")
    void testEvaluateWithEmployedCustomer() {
        customer.setEmploymentStatus(EmploymentStatus.EMPLOYED.name());
        context = ScoringContext.builder()
                .customer(customer)
                .averagePreTaxes(BigDecimal.valueOf(5000))
                .vehicleInvestment(BigDecimal.valueOf(10000))
                .build();

        assertTrue(investmentVsIncomeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when self-employed and vehicle investment <= 3x gross income")
    void testEvaluateWithSelfEmployedAndValidInvestment() {
        customer.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED.name());
        // gross limit: 5000 * 3 = 15000
        context = ScoringContext.builder()
                .customer(customer)
                .averagePreTaxes(BigDecimal.valueOf(5000))
                .vehicleInvestment(BigDecimal.valueOf(15000))
                .build();

        assertTrue(investmentVsIncomeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when self-employed and vehicle investment < 3x gross income")
    void testEvaluateWithSelfEmployedAndLowerInvestment() {
        customer.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED.name());
        context = ScoringContext.builder()
                .customer(customer)
                .averagePreTaxes(BigDecimal.valueOf(5000))
                .vehicleInvestment(BigDecimal.valueOf(14000))
                .build();

        assertTrue(investmentVsIncomeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when self-employed and vehicle investment > 3x gross income")
    void testEvaluateWithSelfEmployedAndExcessiveInvestment() {
        customer.setEmploymentStatus(EmploymentStatus.SELF_EMPLOYED.name());
        // gross limit: 5000 * 3 = 15000
        context = ScoringContext.builder()
                .customer(customer)
                .averagePreTaxes(BigDecimal.valueOf(5000))
                .vehicleInvestment(BigDecimal.valueOf(16000))
                .build();

        assertFalse(investmentVsIncomeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when employment status is null")
    void testEvaluateWithNullEmploymentStatus() {
        customer.setEmploymentStatus(null);
        context = ScoringContext.builder()
                .customer(customer)
                .averagePreTaxes(BigDecimal.valueOf(5000))
                .vehicleInvestment(BigDecimal.valueOf(20000))
                .build();

        assertTrue(investmentVsIncomeRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about investment proportionality")
    void testGetMessage() {
        String message = investmentVsIncomeRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("inversión"));
    }
}
