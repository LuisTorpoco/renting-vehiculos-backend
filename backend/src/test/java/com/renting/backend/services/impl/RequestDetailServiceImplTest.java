package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.ExtraResponseDTO;
import com.renting.backend.dtos.response.RequestDetailResponseDTO;
import com.renting.backend.dtos.response.RequestWithDetailsResponseDTO;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Extra;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.entities.Vehicle;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RequestDetailServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ExtraRepository extraRepository;

    @InjectMocks
    private RequestDetailServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRequestWithDetails_success() {
        // Preparar request
        Customer customer = new Customer();
        customer.setId(10L);
        customer.setName("Juan");
        customer.setFirstSurname("Perez");
        customer.setSecondSurname("Garcia");
        customer.setNif("12345678A");

        Request request = new Request();
        request.setId(100L);
        request.setState(null);
        request.setPeriodInMonths(12);
        request.setCreatedAt(LocalDateTime.now().minusDays(5));
        request.setResolutionDate(LocalDateTime.now());
        request.setCustomer(customer);

        // Detalles: un vehiculo con dos extras y otro vehiculo sin extras
        RequestDetail d1 = RequestDetail.builder()
                .requestId(100L)
                .vehicleId(1L)
                .extraId(11L)
                .build();

        RequestDetail d2 = RequestDetail.builder()
                .requestId(100L)
                .vehicleId(1L)
                .extraId(12L)
                .build();

        RequestDetail d3 = RequestDetail.builder()
                .requestId(100L)
                .vehicleId(2L)
                .extraId(null)
                .build();

        request.setDetails(List.of(d1, d2, d3));

        // Vehiculos y extras
        Vehicle v1 = new Vehicle();
        v1.setId(1L);
        v1.setBrand("Toyota");
        v1.setModel("Corolla");
        v1.setLicensePlate("ABC123");
        v1.setPrice(BigDecimal.valueOf(15000));
        v1.setBaseMonthlyFee(BigDecimal.valueOf(300));

        Vehicle v2 = new Vehicle();
        v2.setId(2L);
        v2.setBrand("Ford");
        v2.setModel("Focus");
        v2.setLicensePlate("DEF456");
        v2.setPrice(BigDecimal.valueOf(12000));
        v2.setBaseMonthlyFee(BigDecimal.valueOf(250));

        Extra e11 = new Extra();
        e11.setId(11L);
        e11.setName("GPS");
        e11.setPrice(BigDecimal.valueOf(10));
        e11.setPercentage(null);
        e11.setCategory("A");

        Extra e12 = new Extra();
        e12.setId(12L);
        e12.setName("BabySeat");
        e12.setPrice(BigDecimal.valueOf(5));
        e12.setPercentage(null);
        e12.setCategory("B");

        when(requestRepository.findByIdActive(100L)).thenReturn(Optional.of(request));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(v1));
        when(vehicleRepository.findById(2L)).thenReturn(Optional.of(v2));
        when(extraRepository.findById(11L)).thenReturn(Optional.of(e11));
        when(extraRepository.findById(12L)).thenReturn(Optional.of(e12));

        RequestWithDetailsResponseDTO response = service.getRequestWithDetails(100L);

        assertNotNull(response);
        assertEquals(100L, response.getRequestId());
        assertEquals(2, response.getVehicles().size());

        RequestDetailResponseDTO vehicle1 = response.getVehicles().stream()
                .filter(v -> v.getVehicleId().equals(1L))
                .findFirst().orElse(null);
        assertNotNull(vehicle1);
        assertEquals("Toyota", vehicle1.getBrand());
        assertEquals(2, vehicle1.getExtras().size());

        RequestDetailResponseDTO vehicle2 = response.getVehicles().stream()
                .filter(v -> v.getVehicleId().equals(2L))
                .findFirst().orElse(null);
        assertNotNull(vehicle2);
        assertEquals("Ford", vehicle2.getBrand());
        assertTrue(vehicle2.getExtras().isEmpty());

        // comprobar datos de cliente
        assertEquals(10L, response.getCustomerId());
        assertEquals("Juan", response.getCustomerName());
    }

    @Test
    void getRequestWithDetails_requestNotFound() {
        when(requestRepository.findByIdActive(999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getRequestWithDetails(999L));
    }
}

