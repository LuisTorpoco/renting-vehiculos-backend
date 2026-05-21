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
        // 1. Buscar el vehículo en la base de datos
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        // Inicializar los valores base del vehículo
        BigDecimal finalInvestment = vehicle.getPrice();
        BigDecimal finalMonthlyFee = vehicle.getBaseMonthlyFee();

        BigDecimal extraFixedIncrement = BigDecimal.ZERO;
        BigDecimal extraPercentageIncrement = BigDecimal.ZERO;

        // 2. Procesar Extras evitando duplicados
        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            Set<Long> uniqueExtraIds = new HashSet<>(request.getExtraIds());
            List<Extra> extras = extraRepository.findAllById(uniqueExtraIds);

            // Acumular extras fijos primero
            for (Extra extra : extras) {
                if (extra.getPrice() != null && (extra.getPercentage() == null || extra.getPercentage().compareTo(BigDecimal.ZERO) == 0)) {
                    extraFixedIncrement = extraFixedIncrement.add(extra.getPrice());
                    // En caso de valor fijo se aumenta la inversión el importe del extra por 12
                    finalInvestment = finalInvestment.add(extra.getPrice().multiply(BigDecimal.valueOf(12)));
                }
            }

            // Cuota base intermedia (Base + Fijos) para el cálculo de los porcentajes mixtos
            BigDecimal intermediateMonthlyFee = finalMonthlyFee.add(extraFixedIncrement);

            // Acumular extras porcentuales
            for (Extra extra : extras) {
                if (extra.getPercentage() != null && extra.getPercentage().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal percentageRate = extra.getPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

                    // Incremento en la cuota mensual
                    BigDecimal increment = intermediateMonthlyFee.multiply(percentageRate);
                    extraPercentageIncrement = extraPercentageIncrement.add(increment);

                    // CORRECCIÓN: Se aumenta la inversión en el mismo porcentaje sobre el precio base del vehículo
                    BigDecimal investmentIncrement = vehicle.getPrice().multiply(percentageRate);
                    finalInvestment = finalInvestment.add(investmentIncrement);
                }
            }
        }

        // Totalizar la cuota base con todos los extras incluidos (antes de aplicar plazos)
        finalMonthlyFee = finalMonthlyFee.add(extraFixedIncrement).add(extraPercentageIncrement);

        // 3. Calcular Regla de negocio por Plazo (Meses de contrato)
        int months = (request.getMonths() == null || request.getMonths() <= 0)
                ? 12
                : request.getMonths();

        BigDecimal termAdjustment = BigDecimal.ZERO;

        if (months < 12) {
            // Si el plazo se reduce se aumenta un 10% por mes de reducción
            int missingMonths = 12 - months;
            BigDecimal penaltyRate = BigDecimal.valueOf(missingMonths).multiply(BigDecimal.valueOf(0.10));
            termAdjustment = finalMonthlyFee.multiply(penaltyRate);
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        } else if (months > 12) {
            // Si el plazo se aumenta, la cuota se reduce en un 3% por mes de aumento
            int extraMonths = months - 12;
            BigDecimal discountRate = BigDecimal.valueOf(extraMonths).multiply(BigDecimal.valueOf(0.03));

            // Límite estricto: hasta un máximo de un descuento de un 20%
            BigDecimal maxDiscountRate = BigDecimal.valueOf(0.20);
            if (discountRate.compareTo(maxDiscountRate) > 0) {
                discountRate = maxDiscountRate;
            }

            termAdjustment = finalMonthlyFee.multiply(discountRate).negate();
            finalMonthlyFee = finalMonthlyFee.add(termAdjustment);
        }

        // 4. Construir y retornar la respuesta con escala a 2 decimales
        return PriceCalculationResponse.builder()
                .finalInvestment(finalInvestment.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(finalMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(extraFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(extraPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}

