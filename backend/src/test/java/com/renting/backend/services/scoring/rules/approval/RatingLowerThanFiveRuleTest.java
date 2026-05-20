package com.renting.backend.services.scoring.rules.approval;

import com.renting.backend.entities.Customer;
import com.renting.backend.services.scoring.context.ScoringContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RatingLowerThanFiveRule Tests")
class RatingLowerThanFiveRuleTest {

    private RatingLowerThanFiveRule ratingLowerThanFiveRule;
    private ScoringContext context;
    private Customer customer;

    @BeforeEach
    void setUp() {
        ratingLowerThanFiveRule = new RatingLowerThanFiveRule();
        customer = new Customer();
    }

    @Test
    @DisplayName("Should return true when customer rating is lower than 5")
    void testEvaluateWithRatingLowerThanFive() {
        customer.setScoring(BigDecimal.valueOf(4.5));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(ratingLowerThanFiveRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return true when customer rating is much lower than 5")
    void testEvaluateWithMuchLowerRating() {
        customer.setScoring(BigDecimal.valueOf(2.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertTrue(ratingLowerThanFiveRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer rating equals 5")
    void testEvaluateWithRatingEqualToFive() {
        customer.setScoring(BigDecimal.valueOf(5.0));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(ratingLowerThanFiveRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return false when customer rating is higher than 5")
    void testEvaluateWithRatingHigherThanFive() {
        customer.setScoring(BigDecimal.valueOf(6.5));
        context = ScoringContext.builder()
                .customer(customer)
                .build();

        assertFalse(ratingLowerThanFiveRule.evaluate(context));
    }

    @Test
    @DisplayName("Should return message about rating requirement")
    void testGetMessage() {
        String message = ratingLowerThanFiveRule.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("rating"));
    }
}
