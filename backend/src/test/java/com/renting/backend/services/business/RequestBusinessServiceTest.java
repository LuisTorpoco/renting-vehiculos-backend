package com.renting.backend.services.business;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.RequestVehicleDTO;
import com.renting.backend.dtos.response.RuleEvaluationResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestDetailRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestBusinessServiceTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private RequestDetailRepository detailRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private DenialRulesEngine denialRulesEngine;
    @Mock
    private ApprovalRulesEngine approvalRulesEngine;
    @InjectMocks
    private RequestBusinessService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearRequestClienteNoExiste() {
        // Prueba: Lanza excepción si el cliente no existe
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
    }

    @Test
    void crearRequestConDenegacion() {
        // Prueba: Si alguna regla de denegación pasa, el estado debe ser DENIED
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(Collections.emptyList());
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, true, null)));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Request result = service.create(dto);
        assertEquals(RequestStatus.DENIED, result.getState());
    }

    @Test
    void crearRequestConAprobacion() {
        // Prueba: Si no hay denegación y todas las reglas de aprobación pasan, el estado debe ser APPROVED
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(2L);
        dto.setVehicles(Collections.emptyList());
        Customer customer = new Customer();
        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, false, null)));
        when(approvalRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, true, null)));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Request result = service.create(dto);
        assertEquals(RequestStatus.APPROVED, result.getState());
    }

    @Test
    void crearRequestPendienteSiNoAprueba() {
        // Prueba: Si no hay denegación y no todas las reglas de aprobación pasan, el estado debe ser PENDING_ANALYST
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(3L);
        dto.setVehicles(Collections.emptyList());
        Customer customer = new Customer();
        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, false, null)));
        when(approvalRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, false, null)));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Request result = service.create(dto);
        assertEquals(RequestStatus.PENDING_ANALYST, result.getState());
    }
}
