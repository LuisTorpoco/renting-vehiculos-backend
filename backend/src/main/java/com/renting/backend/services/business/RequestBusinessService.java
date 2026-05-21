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

    private final DenialRulesEngine denialRulesEngine;
    private final ApprovalRulesEngine approvalRulesEngine;

    @Transactional
    public Request create(CreateRequestDTO dto) {

        //Buscamos el cliente en Oracle
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + dto.getCustomerId()
                ));

        //Construimos el contexto de evaluación
        ScoringContext context = ScoringContext.builder()
                .customer(customer)
                .build();

        // Estado base por defecto
        RequestStatus finalStatus = RequestStatus.PENDING_ANALYST;

        // Motor de Denegaciones (Estrategia Fail-Fast / Cortocircuito)
        List<RuleEvaluationResponse> denialEvaluations = denialRulesEngine.evaluate(context);
        boolean shouldBeDenied = denialEvaluations.stream()
                .anyMatch(RuleEvaluationResponse::getPassed);

        if (shouldBeDenied) {
            finalStatus = RequestStatus.DENIED;
        } else {

            //Motor de Aprobaciones (Solo si no hay denegaciones)
            List<RuleEvaluationResponse> approvalEvaluations = approvalRulesEngine.evaluate(context);

            // Se deben cumplir absolutamente todas las reglas del motor de aprobación
            boolean meetsAllApprovals = !approvalEvaluations.isEmpty() && approvalEvaluations.stream()
                    .allMatch(RuleEvaluationResponse::getPassed);

            if (meetsAllApprovals) {
                finalStatus = RequestStatus.APPROVED;
            }
        }

        // Creacion de la Request con el estado dinámico correcto
        Request request = Request.builder()
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .periodInMonths(dto.getPeriodInMonths())
                .state(finalStatus)
                .isActive(1)
                .build();

        Request savedRequest = requestRepository.save(request);


        saveRequestDetails(savedRequest, dto);

        return savedRequest;
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