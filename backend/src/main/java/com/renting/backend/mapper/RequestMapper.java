package com.renting.backend.mapper;

import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestResponseDTO toDTO(Request request) {

        return RequestResponseDTO.builder()
                .id(request.getId())
                .customerId(request.getCustomerId())
                .status(request.getState())
                .periodInMonths(request.getPeriodInMonths())
                .createdAt(request.getCreatedAt())
                .resolutionDate(request.getResolutionDate())
                .build();
    }
}
