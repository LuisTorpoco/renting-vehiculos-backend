package com.renting.backend.services.impl;

import com.renting.backend.mapper.RequestMapper;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.business.RequestBusinessService;
import com.renting.backend.validations.RequestValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

@Import(MockitoExtension.class)
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

//    @Test
//    void shouldReturnResponseWithValidRequest(){
//        var CreateRequestDto = new CreateRequestDTO();
//        CreateRequestDto.setCustomerId(1L);
//        CreateRequestDto.setPeriodInMonths(24);
//        CreateRequestDto.setVehicles(List.of());
//
//        when(businessService.create(CreateRequestDto)).thenReturn(new Request());
//    }
}