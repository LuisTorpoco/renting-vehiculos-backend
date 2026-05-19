package com.renting.backend.services.business;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.RequestVehicleDTO;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestDetailRepository;
import com.renting.backend.repositories.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestBusinessService {

    private final RequestRepository requestRepository;
    private final RequestDetailRepository detailRepository;
    private final CustomerRepository customerRepository;

    public Request create(CreateRequestDTO dto) {

        Customer customer =
                customerRepository
                        .findById(
                                dto.getCustomerId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with id: "
                                                + dto.getCustomerId()
                                )
                        );

        Request request = Request.builder()
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .periodInMonths(dto.getPeriodInMonths())
                .state(RequestStatus.PENDING_ANALYST)
                .isActive(1)
                .build();

        Request savedRequest = requestRepository.save(request);

        saveRequestDetails(savedRequest, dto);

        return savedRequest;
    }

    private void saveRequestDetails(
            Request request,
            CreateRequestDTO dto
    ) {

        for (RequestVehicleDTO vehicle : dto.getVehicles()) {

            RequestDetail detail = RequestDetail.builder()
                    .requestId(request.getId())
                    .vehicleId(vehicle.getVehicleId())
                    .extraId(vehicle.getExtraId())
                    .build();

            detailRepository.save(detail);
        }
    }
}