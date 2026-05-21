package com.renting.backend.services.business;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.RequestVehicleDTO;
import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestDetailRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestBusinessService {

    private final RequestRepository requestRepository;
    private final RequestDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final ScoringService scoringService;

    public Request create(CreateRequestDTO dto) {

        Customer customer =
                customerRepository
                        .findById(dto.getCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with id: "
                                                + dto.getCustomerId()
                                )
                        );

        // 1. Guardar solicitud con estado inicial PENDING_ANALYST
        Request request = Request.builder()
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .periodInMonths(dto.getPeriodInMonths())
                .state(RequestStatus.PENDING_ANALYST)
                .isActive(1)
                .build();

        Request savedRequest = requestRepository.save(request);

        // 2. Guardar detalles de vehículos y extras
        saveRequestDetails(savedRequest, dto);

        // 3. Llamar al scoring con el cliente y la solicitud guardada
        ScoringResponse scoring = scoringService.evaluate(customer, savedRequest);

        // 4. Aplicar resultado del scoring
        if (Boolean.TRUE.equals(scoring.getAutomaticallyDenied())) {
            savedRequest.setState(RequestStatus.DENIED);
            savedRequest.setResolutionDate(LocalDateTime.now());
            savedRequest.setIsActive(0);

        } else if (Boolean.TRUE.equals(scoring.getAutomaticallyApproved())) {
            savedRequest.setState(RequestStatus.APPROVED);
            savedRequest.setResolutionDate(LocalDateTime.now());
            savedRequest.setIsActive(0);
        }
        // Si ninguno → queda PENDING_ANALYST con isActive=1

        // 5. Guardar con estado final
        return requestRepository.save(savedRequest);
    }

    private void saveRequestDetails(Request request, CreateRequestDTO dto) {

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
