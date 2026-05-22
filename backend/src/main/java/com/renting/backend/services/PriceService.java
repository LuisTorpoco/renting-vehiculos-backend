package com.renting.backend.services;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Extra;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final VehicleRepository vehicleRepository;
    private final ExtraRepository extraRepository;

    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        
        BigDecimal carPrice = vehicle.getPrice();
        BigDecimal baseMonthlyFee = vehicle.getBaseMonthlyFee();

        BigDecimal extraFixedIncrement = BigDecimal.ZERO;
        BigDecimal extraPercentageIncrement = BigDecimal.ZERO;


        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            Set<Long> uniqueExtraIds = new HashSet<>(request.getExtraIds());
            List<Extra> extras = extraRepository.findAllById(uniqueExtraIds);

            // Acumular extras fijos primero
            for (Extra extra : extras) {
                if (extra.getPrice() != null && (extra.getPercentage() == null || extra.getPercentage().compareTo(BigDecimal.ZERO) == 0)) {
                    extraFixedIncrement = extraFixedIncrement.add(extra.getPrice());
                    // Los extras fijos aumentan la inversión total anualizada
                    carPrice = carPrice.add(extra.getPrice().multiply(BigDecimal.valueOf(12)));
                }
            }

            BigDecimal intermediateMonthlyFee = baseMonthlyFee.add(extraFixedIncrement);

            // Acumular extras porcentuales / mixtos
            for (Extra extra : extras) {
                if (extra.getPercentage() != null && extra.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentageRate = extra.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                    BigDecimal increment = intermediateMonthlyFee.multiply(percentageRate);
                    extraPercentageIncrement = extraPercentageIncrement.add(increment);
                    carPrice = carPrice.add(increment.multiply(BigDecimal.valueOf(12)));
                }
            }
        }


        BigDecimal totalBaseMonthlyFee = baseMonthlyFee.add(extraFixedIncrement).add(extraPercentageIncrement);


        Integer monthsObj = request.getMonths();
        int months = (monthsObj == null || monthsObj <= 0) ? 12 : monthsObj;


        BigDecimal termAdjustment = BigDecimal.ZERO;
        if (months < 36) {

            termAdjustment = totalBaseMonthlyFee.multiply(BigDecimal.valueOf(0.05));
        }


        BigDecimal finalMonthlyFee = totalBaseMonthlyFee.add(termAdjustment);

        return PriceCalculationResponse.builder()
                .finalInvestment(carPrice.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(finalMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(extraFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(extraPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}