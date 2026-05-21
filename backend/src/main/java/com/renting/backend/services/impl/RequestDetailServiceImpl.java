package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.*;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.ExtraRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestDetailServiceImpl implements com.renting.backend.services.RequestDetailService {

    private final RequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final ExtraRepository extraRepository;

    @Override
    public RequestWithDetailsResponseDTO getRequestWithDetails(Long requestId) {

        // 1. Buscar la solicitud
        Request request = requestRepository.findByIdActive(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Request not found with id: " + requestId
                ));

        // 2. Obtener detalles de request_detail agrupados por vehículo
        Map<Long, List<RequestDetail>> vehicleDetailsMap = request.getDetails()
                .stream()
                .collect(Collectors.groupingBy(RequestDetail::getVehicleId));

        // 3. Construir la lista de vehículos sin repetir
        List<RequestDetailResponseDTO> vehicles = vehicleDetailsMap.entrySet()
                .stream()
                .map(entry -> {
                    Long vehicleId = entry.getKey();
                    List<RequestDetail> details = entry.getValue();

                    // Obtener datos del vehículo
                    var vehicle = vehicleRepository.findById(vehicleId)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Vehicle not found with id: " + vehicleId
                            ));

                    // Obtener lista de extras para este vehículo
                    List<ExtraResponseDTO> extras = details.stream()
                            .filter(detail -> detail.getExtraId() != null)
                            .map(detail -> {
                                var extra = extraRepository.findById(detail.getExtraId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                "Extra not found with id: " + detail.getExtraId()
                                        ));

                                return ExtraResponseDTO.builder()
                                        .id(extra.getId())
                                        .name(extra.getName())
                                        .price(extra.getPrice())
                                        .percentage(extra.getPercentage())
                                        .category(extra.getCategory())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return RequestDetailResponseDTO.builder()
                            .vehicleId(vehicle.getId())
                            .brand(vehicle.getBrand())
                            .model(vehicle.getModel())
                            .licensePlate(vehicle.getLicensePlate())
                            .price(vehicle.getPrice())
                            .baseMonthlyFee(vehicle.getBaseMonthlyFee())
                            .extras(extras)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Construir respuesta final
        return RequestWithDetailsResponseDTO.builder()
                .requestId(request.getId())
                .state(request.getState())
                .periodInMonths(request.getPeriodInMonths())
                .createdAt(request.getCreatedAt())
                .resolutionDate(request.getResolutionDate())
                .customerId(request.getCustomer().getId())
                .customerName(request.getCustomer().getName())
                .customerFirstSurname(request.getCustomer().getFirstSurname())
                .customerSecondSurname(request.getCustomer().getSecondSurname())
                .customerNif(request.getCustomer().getNif())
                .vehicles(vehicles)
                .build();
    }
}
