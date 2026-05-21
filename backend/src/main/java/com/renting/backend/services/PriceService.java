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
        //Buscar el vehículo en la base de datos
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + request.getVehicleId()));

        // Valores base extraídos de las tablas de Oracle
        BigDecimal carPrice = vehicle.getPrice();                // Ej: 15000
        BigDecimal baseMonthlyFee = vehicle.getBaseMonthlyFee(); // Ej: 250

        BigDecimal extraFixedIncrement = BigDecimal.ZERO;
        BigDecimal extraPercentageIncrement = BigDecimal.ZERO;

        // Procesar Extras evitando duplicados (Mantenemos la limpieza de la Tarea 5)
        if (request.getExtraIds() != null && !request.getExtraIds().isEmpty()) {
            Set<Long> uniqueExtraIds = new HashSet<>(request.getExtraIds());
            List<Extra> extras = extraRepository.findAllById(uniqueExtraIds);

            // Acumular extras fijos primero
            for (Extra extra : extras) {
                if (extra.getPrice() != null && (extra.getPercentage() == null || extra.getPercentage().compareTo(BigDecimal.ZERO) == 0)) {
                    extraFixedIncrement = extraFixedIncrement.add(extra.getPrice());
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

        //Sumamos los extras correspondientes a la cuota base
        baseMonthlyFee = baseMonthlyFee.add(extraFixedIncrement).add(extraPercentageIncrement);


        int months = request.getMonths();
        if (months <= 0) {
            months = 12;
        }


        BigDecimal difference = carPrice.subtract(baseMonthlyFee);


        BigDecimal termAdjustment = difference.divide(BigDecimal.valueOf(months), 4, RoundingMode.HALF_UP);

        BigDecimal finalMonthlyFee = baseMonthlyFee.add(termAdjustment);

        return PriceCalculationResponse.builder()
                .finalInvestment(carPrice.setScale(2, RoundingMode.HALF_UP))
                .finalMonthlyFee(finalMonthlyFee.setScale(2, RoundingMode.HALF_UP))
                .extraFixedIncrement(extraFixedIncrement.setScale(2, RoundingMode.HALF_UP))
                .extraPercentageIncrement(extraPercentageIncrement.setScale(2, RoundingMode.HALF_UP))
                .termAdjustment(termAdjustment.setScale(2, RoundingMode.HALF_UP)) // Esto reflejará el coste prorrateado por mes
                .build();
    }
}