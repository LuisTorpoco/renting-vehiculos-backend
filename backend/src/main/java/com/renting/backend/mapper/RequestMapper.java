package com.renting.backend.mapper;

import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Request;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {

    public RequestResponseDTO toDTO(Request request) {

        List<Long> vehicleIds = request.getDetails() == null ? null : request.getDetails()
                .stream()
                .map(RequestDetail -> RequestDetail.getVehicleId())
                .distinct()
                .collect(Collectors.toList());

        return RequestResponseDTO.builder()
                .id(request.getId())
                .customerId(request.getCustomer().getId())
                .status(request.getState())
                .periodInMonths(request.getPeriodInMonths())
                .createdAt(request.getCreatedAt())
                .resolutionDate(request.getResolutionDate())
                .vehicleIds(vehicleIds)
                .build();
    }
}
