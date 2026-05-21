package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmploymentSeniorityRule Tests")
class EmploymentSeniorityRuleTest {

    private EmploymentSeniorityRule employmentSeniorityRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        employmentSeniorityRule = new EmploymentSeniorityRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when employment seniority is 3 years or more")
    void testEvaluateWithValidSeniority() {
        customer.setCareerTime(LocalDate.now().minusYears(3));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(employmentSeniorityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when employment seniority is more than 3 years")
    void testEvaluateWithMoreThanThreeYears() {
        customer.setCareerTime(LocalDate.now().minusYears(5));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(employmentSeniorityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when employment seniority is less than 3 years")
    void testEvaluateWithInsufficientSeniority() {
        customer.setCareerTime(LocalDate.now().minusYears(2));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(employmentSeniorityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when employment seniority is less than 1 year")
    void testEvaluateWithLessThanOneYear() {
        customer.setCareerTime(LocalDate.now().minusMonths(6));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(employmentSeniorityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when careerTime is null")
    void testEvaluateWithNullCareerTime() {
        customer.setCareerTime(null);
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(employmentSeniorityRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about employment seniority requirement")
    void testGetMessage() {
        String message = employmentSeniorityRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("3 años"));
    }
}
