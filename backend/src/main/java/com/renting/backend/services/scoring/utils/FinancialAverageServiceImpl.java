package com.renting.backend.services.scoring.utils;

import com.renting.backend.entities.Income;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialAverageServiceImpl
        implements FinancialAverageService {

    private static final int
            YEARS_TO_ANALYZE = 3;

    private static final int
            SCALE = 2;

    @Override
    public BigDecimal calculateAveragePreTaxes(
            List<Income> incomes
    ) {

        return calculateAverage(
                incomes,
                true
        );
    }

    @Override
    public BigDecimal calculateAveragePostTaxes(
            List<Income> incomes
    ) {

        return calculateAverage(
                incomes,
                false
        );
    }

    private BigDecimal calculateAverage(
            List<Income> incomes,
            boolean preTaxes
    ) {

        if (incomes == null
                || incomes.isEmpty()) {

            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }

        LocalDateTime limitDate =
                LocalDateTime.now()
                        .minusYears(
                                YEARS_TO_ANALYZE
                        );

        List<Income> validIncomes =
                incomes.stream()
                        .filter(income ->
                                income
                                        .getCreatedAt()
                                        .isAfter(limitDate)
                        )
                        .toList();

        if (!validIncomes.isEmpty()) {

            BigDecimal total =
                    validIncomes.stream()
                            .map(income ->
                                    preTaxes
                                            ? income.getPreTaxes()
                                            : income.getPostTaxes()
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );

            return total.divide(
                    BigDecimal.valueOf(
                            validIncomes.size()
                    ),
                    SCALE,
                    RoundingMode.HALF_UP
            );
        }

        Income latestIncome =
                incomes.get(0);

        LocalDateTime twoYearsAgo =
                LocalDateTime.now()
                        .minusYears(2);

        if (latestIncome
                .getCreatedAt()
                .isAfter(twoYearsAgo)) {

            BigDecimal value = preTaxes
                    ? latestIncome
                    .getPreTaxes()
                    : latestIncome
                    .getPostTaxes();

            return value == null ? BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP) : value.setScale(SCALE, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
    }
}