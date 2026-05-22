package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.mapper.RequestMapper;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.business.RequestBusinessService;
import com.renting.backend.validations.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    RequestRepository requestRepository;

    @Mock
    RequestBusinessService businessService;

    @Mock
    RequestValidator validator;

    @Mock
    RequestMapper mapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void shouldReturnResponseWithValidRequest(){
        var createRequestDto = new CreateRequestDTO();
        createRequestDto.setCustomerId(1L);
        createRequestDto.setPeriodInMonths(24);
        createRequestDto.setVehicles(List.of());

        Request request = new Request();
        request.setId(1L);
        Customer c = new Customer();
        c.setId(1L);
        request.setCustomer(c);
        request.setState(RequestStatus.PENDING_ANALYST);

        when(businessService.create(createRequestDto)).thenReturn(request);

        RequestResponseDTO expectedDto = RequestResponseDTO.builder()
                .id(1L)
                .customerId(1L)
                .status(RequestStatus.PENDING_ANALYST)
                .build();

        when(mapper.toDTO(request)).thenReturn(expectedDto);

        RequestResponseDTO response = requestService.createRequest(createRequestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(RequestStatus.PENDING_ANALYST, response.getStatus());
    }
}