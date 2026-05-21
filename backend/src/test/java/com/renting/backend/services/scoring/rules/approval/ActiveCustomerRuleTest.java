package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActiveCustomerRule Tests")
class ActiveCustomerRuleTest {

    private ActiveCustomerRule activeCustomerRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        activeCustomerRule = new ActiveCustomerRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when customer is active (isActive = 1)")
    void testEvaluateWithActiveCustomer() {
        customer.setIsActive(1);
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(activeCustomerRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer is inactive (isActive = 0)")
    void testEvaluateWithInactiveCustomer() {
        customer.setIsActive(0);
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(activeCustomerRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when isActive is null")
    void testEvaluateWithNullIsActive() {
        customer.setIsActive(null);
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(activeCustomerRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about active account verification")
    void testGetMessage() {
        String message = activeCustomerRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("activa"));
        assertEquals("La cuenta del cliente está activa y verificada de manera positiva.", message);
    }
}
