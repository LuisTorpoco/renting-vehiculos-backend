package com.renting.backend.controllers;

import com.renting.backend.repositories.VehicleRepository;
import com.renting.backend.services.PriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock private PriceService priceService;
    @Mock private VehicleRepository vehicleRepository;
    @InjectMocks private VehicleController vehicleController;


}