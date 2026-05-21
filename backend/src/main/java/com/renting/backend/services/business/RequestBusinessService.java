package com.renting.backend.services.business;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.RequestVehicleDTO;
<<<<<<< HEAD
import com.renting.backend.dtos.response.ScoringResponse;
=======
import com.renting.backend.dtos.response.RuleEvaluationResponse;
>>>>>>> develop
import com.renting.backend.entities.Customer;
import com.renting.backend.entities.Request;
import com.renting.backend.entities.RequestDetail;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestDetailRepository;
import com.renting.backend.repositories.RequestRepository;
<<<<<<< HEAD
import com.renting.backend.services.ScoringService;
=======
import com.renting.backend.services.scoring.context.ScoringContext;
import com.renting.backend.services.scoring.engine.DenialRulesEngine;
import com.renting.backend.services.scoring.engine.ApprovalRulesEngine;
>>>>>>> develop
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestBusinessService {

    private final RequestRepository requestRepository;
    private final RequestDetailRepository detailRepository;
    private final CustomerRepository customerRepository;
    private final ScoringService scoringService;

    private final DenialRulesEngine denialRulesEngine;
    private final ApprovalRulesEngine approvalRulesEngine;

    @Transactional
    public Request create(CreateRequestDTO dto) {

<<<<<<< HEAD
        Customer customer =
                customerRepository
                        .findById(dto.getCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with id: "
                                                + dto.getCustomerId()
                                )
                        );
=======

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + dto.getCustomerId()
                ));


        ScoringContext context = ScoringContext.builder()
                .customer(customer)
                .build();


        RequestStatus finalStatus = RequestStatus.PENDING_ANALYST;


        List<RuleEvaluationResponse> denialEvaluations = denialRulesEngine.evaluate(context);
        boolean shouldBeDenied = denialEvaluations.stream()
                .anyMatch(RuleEvaluationResponse::getPassed);

        if (shouldBeDenied) {
            finalStatus = RequestStatus.DENIED;
        } else {


            List<RuleEvaluationResponse> approvalEvaluations = approvalRulesEngine.evaluate(context);
            boolean meetsAllApprovals = !approvalEvaluations.isEmpty() && approvalEvaluations.stream()
                    .allMatch(RuleEvaluationResponse::getPassed);

            if (meetsAllApprovals) {
                finalStatus = RequestStatus.APPROVED;
            }
        }

        LocalDateTime now = LocalDateTime.now();

>>>>>>> develop

        // 1. Guardar solicitud con estado inicial PENDING_ANALYST
        Request request = Request.builder()
                .customer(customer)
                .createdAt(now)
                .state(finalStatus)
                .resolutionDate(now)
                .periodInMonths(dto.getPeriodInMonths())
                .isActive(1)
                .build();

        Request savedRequest = requestRepository.save(request);

<<<<<<< HEAD
        // 2. Guardar detalles de vehículos y extras
=======

>>>>>>> develop
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
<<<<<<< HEAD

=======
>>>>>>> develop
        for (RequestVehicleDTO vehicle : dto.getVehicles()) {

            List<Long> extraIds = vehicle.getExtraIds();


            if (extraIds != null && !extraIds.isEmpty()) {
                for (Long extraId : extraIds) {
                    RequestDetail detail = RequestDetail.builder()
                            .requestId(request.getId())
                            .vehicleId(vehicle.getVehicleId())
                            .extraId(extraId)
                            .build();

                    detailRepository.save(detail);
                }
            } else {

                RequestDetail detail = RequestDetail.builder()
                        .requestId(request.getId())
                        .vehicleId(vehicle.getVehicleId())
                        .extraId(null)
                        .build();

                detailRepository.save(detail);
            }
        }
    }
}
