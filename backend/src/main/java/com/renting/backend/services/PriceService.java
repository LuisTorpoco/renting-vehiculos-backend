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
        //Buscar el vehículo en la base de datos Oracle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        // Inicializar valores base del vehículo
        BigDecimal finalInvestment = vehicle.getPrice();
        BigDecimal finalMonthlyFee = vehicle.getBaseMonthlyFee();

        BigDecimal extraFixedIncrement = BigDecimal.ZERO;
        BigDecimal extraPercentageIncrement = BigDecimal.ZERO;


        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            // Eliminamos IDs duplicados para blindar el servicio contra payloads corruptos
            Set<Long> uniqueExtraIds = new HashSet<>(request.getExtraIds());
            List<Extra> extras = extraRepository.findAllById(uniqueExtraIds);


            for (Extra extra : extras) {
                if (extra.getPrice() != null && (extra.getPercentage() == null || extra.getPercentage().compareTo(BigDecimal.ZERO) == 0)) {
                    extraFixedIncrement = extraFixedIncrement.add(extra.getPrice());

                    finalInvestment = finalInvestment.add(extra.getPrice().multiply(BigDecimal.valueOf(12)));
                }
            }

            // Actualizamos la cuota base intermedia (Base + Fijados) para que el cálculo MIXTO sea correcto
            BigDecimal intermediateMonthlyFee = finalMonthlyFee.add(extraFixedIncrement);


            for (Extra extra : extras) {
                if (extra.getPercentage() != null && extra.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentageRate = extra.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);


                    BigDecimal increment = intermediateMonthlyFee.multiply(percentageRate);
                    extraPercentageIncrement = extraPercentageIncrement.add(increment);


                    finalInvestment = finalInvestment.add(increment.multiply(BigDecimal.valueOf(12)));
                }
            }
        }

        // Aplicamos todos los incrementos consolidados a la cuota mensual final
        finalMonthlyFee = finalMonthlyFee.add(extraFixedIncrement).add(extraPercentageIncrement);

        // Calcular Regla de negocio por Plazo (Meses de contrato) - Tu lógica es perfecta aquí
        int months = request.getMonths();
        BigDecimal termAdjustment = BigDecimal.ZERO;

        if (months < 12) {
            int missingMonths = 12 - months;
            BigDecimal penaltyRate = BigDecimal.valueOf(missingMonths).multiply(BigDecimal.valueOf(0.10));
            termAdjustment = finalMonthlyFee.multiply(penaltyRate);
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        } else if (months > 12) {
            int extraMonths = months - 12;
            BigDecimal discountRate = BigDecimal.valueOf(extraMonths).multiply(BigDecimal.valueOf(0.03));

            //Límite estricto : Máximo 20% de descuento
            BigDecimal maxDiscountRate = BigDecimal.valueOf(0.20);
            if (discountRate.compareTo(maxDiscountRate) > 0) {
                discountRate = maxDiscountRate;
            }

            termAdjustment = finalMonthlyFee.multiply(discountRate).negate();
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        }

        return PriceCalculationResponse.builder()
                .finalInvestment(finalInvestment.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(finalMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(extraFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(extraPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}