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
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Test
    @DisplayName("Throws expetion if customer doesnt exists")
    void shouldThrowExceptionIfClientDoesntExists() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        assertThrows(ResourceNotFoundException.class, () -> service.create(dto));
        verifyNoInteractions(denialRulesEngine, approvalRulesEngine, requestRepository, detailRepository);
    }

    @Test
    @DisplayName("Throws exception if some denial rule passes")
    void shouldDenyNewRequest() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(Collections.emptyList());
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(List.of(new RuleEvaluationResponse(null, true, null)));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Request result = service.create(dto);
        assertEquals(RequestStatus.DENIED, result.getState());
        verifyNoInteractions(approvalRulesEngine);
    }

    @Test
    @DisplayName("Auto approve new request if all approval rule passes and denials doesnt")
    void shouldApproveNewRequest() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(3L);
        dto.setVehicles(Collections.emptyList());

        Customer customer = new Customer();
        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(
                List.of(new RuleEvaluationResponse(null, false, null)));
        when(approvalRulesEngine.evaluate(any())).thenReturn(
                List.of(new RuleEvaluationResponse(null, true, null)));
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Request result = service.create(dto);

        assertEquals(RequestStatus.APPROVED, result.getState());
    }

    @Test
    @DisplayName("Set on pending the request if doesnt pass all approval rules and doesnt pass any denial rule")
    void shouldCreateAPendingRequestIfDoesntPassApprovalRules() {
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

    @Test
    void shouldCreateAPendingRequestIfDoesntPassAnyRule(){
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(3L);
        dto.setVehicles(Collections.emptyList());
        Customer customer = new Customer();
        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(denialRulesEngine.evaluate(any())).thenReturn(
                List.of(new RuleEvaluationResponse(null, false, null)));
        when(approvalRulesEngine.evaluate(any())).thenReturn(List.of());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Request result = service.create(dto);

        assertEquals(RequestStatus.PENDING_ANALYST, result.getState());
    }

    @Test
    @DisplayName("Should save the request exactly once")
    void shouldSaveRequestExactlyOnce(){
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(List.of());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(approvalRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.create(dto);

        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    @DisplayName("Should set status PENDING_ANALYST when approval rules list is empty")
    void shouldSetStatusPendingWhenApprovalRulesListIsEmpty() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(Collections.emptyList());

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(
                List.of(new RuleEvaluationResponse(null, false, null))
        );
        when(approvalRulesEngine.evaluate(any())).thenReturn(List.of());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Request result = service.create(dto);

        assertEquals(RequestStatus.PENDING_ANALYST, result.getState());
    }

    @Test
    @DisplayName("Should save one detail per extraId when vehicle has multiple extraIds")
    void shouldSaveOneDetailPerExtraId() {
        RequestVehicleDTO vehicle = new RequestVehicleDTO();
        vehicle.setVehicleId(10L);
        vehicle.setExtraIds(List.of(1L, 2L, 3L));

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(List.of(vehicle));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(approvalRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.create(dto);

        verify(detailRepository, times(3)).save(any(RequestDetail.class));
    }

    @Test
    @DisplayName("Should save one detail with null extraId when vehicle has null extraIds")
    void shouldSaveOneDetailWithNullExtraIdWhenExtraIdsIsNull() {
        RequestVehicleDTO vehicle = new RequestVehicleDTO();
        vehicle.setVehicleId(10L);
        vehicle.setExtraIds(null);

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(List.of(vehicle));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(approvalRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.create(dto);

        verify(detailRepository, times(1)).save(argThat(detail -> detail.getExtraId() == null));
    }

    @Test
    @DisplayName("Should save one detail with null extraId when vehicle has empty extraIds list")
    void shouldSaveOneDetailWithNullExtraIdWhenExtraIdsIsEmpty() {
        RequestVehicleDTO vehicle = new RequestVehicleDTO();
        vehicle.setVehicleId(10L);
        vehicle.setExtraIds(Collections.emptyList());

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(List.of(vehicle));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(approvalRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.create(dto);

        verify(detailRepository, times(1)).save(argThat(detail -> detail.getExtraId() == null));
    }

    @Test
    @DisplayName("Should save correct number of details for multiple vehicles")
    void shouldSaveCorrectDetailsForMultipleVehicles() {
        RequestVehicleDTO vehicleWithExtras = new RequestVehicleDTO();
        vehicleWithExtras.setVehicleId(10L);
        vehicleWithExtras.setExtraIds(List.of(1L, 2L));

        RequestVehicleDTO vehicleWithoutExtras = new RequestVehicleDTO();
        vehicleWithoutExtras.setVehicleId(20L);
        vehicleWithoutExtras.setExtraIds(null);

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setCustomerId(1L);
        dto.setVehicles(List.of(vehicleWithExtras, vehicleWithoutExtras));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(denialRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(approvalRulesEngine.evaluate(any())).thenReturn(Collections.emptyList());
        when(requestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        service.create(dto);

        verify(detailRepository, times(3)).save(any(RequestDetail.class));
    }
}
