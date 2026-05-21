package com.renting.backend.services;

import com.renting.backend.dtos.request.PriceCalculationRequest;
import com.renting.backend.dtos.response.PriceCalculationResponse;
import com.renting.backend.entities.Extra;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceTest {
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ExtraRepository extraRepository;
    @InjectMocks
    private PriceService priceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calcularPrecioSinExtras() {

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(BigDecimal.valueOf(15000));
        vehicle.setBaseMonthlyFee(BigDecimal.valueOf(250));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        PriceCalculationResponse response = priceService.calculatePrice(request);
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(15000), response.getFinalInvestment());
        assertEquals(BigDecimal.valueOf(250), response.getFinalMonthlyFee());
    }

    @Test
    void calcularPrecioConExtras() {
        // Prueba: Calcular precio con extras fijos y porcentuales
        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(1L);
        request.setExtraIds(List.of(2L, 3L));
        Vehicle vehicle = new Vehicle();
        vehicle.setPrice(BigDecimal.valueOf(20000));
        vehicle.setBaseMonthlyFee(BigDecimal.valueOf(300));
        Extra extraFijo = new Extra();
        extraFijo.setId(2L);
        extraFijo.setPrice(BigDecimal.valueOf(100));
        extraFijo.setPercentage(null);
        Extra extraPorcentaje = new Extra();
        extraPorcentaje.setId(3L);
        extraPorcentaje.setPrice(null);
        extraPorcentaje.setPercentage(BigDecimal.valueOf(10));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(extraRepository.findAllById(any())).thenReturn(List.of(extraFijo, extraPorcentaje));
        PriceCalculationResponse response = priceService.calculatePrice(request);
        assertNotNull(response);
        assertTrue(response.getFinalInvestment().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(response.getFinalMonthlyFee().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void lanzarExcepcionSiVehiculoNoExiste() {

        PriceCalculationRequest request = new PriceCalculationRequest();
        request.setVehicleId(99L);
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () -> priceService.calculatePrice(request));
        assertTrue(exception.getMessage().contains("Vehículo no encontrado"));
    }
}
