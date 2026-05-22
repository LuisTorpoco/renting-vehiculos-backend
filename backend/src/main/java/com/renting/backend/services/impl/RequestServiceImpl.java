package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.dtos.response.ScoringResponse;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.mapper.RequestMapper;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.RequestService;
import com.renting.backend.services.ScoringService;
import com.renting.backend.services.business.RequestBusinessService;
import com.renting.backend.validations.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestBusinessService businessService;
    private final RequestValidator validator;
    private final RequestMapper mapper;
    private final ScoringService scoringService;

    @Override
    public RequestResponseDTO createRequest(CreateRequestDTO dto) {
        // 1. Persistimos la solicitud base en la base de datos
        Request request = businessService.create(dto);

        // 2. Evaluamos los riesgos de forma dinámica pasando la entidad Request completa
        if (request != null && request.getCustomer() != null) {
            ScoringResponse scoring = scoringService.evaluate(request.getCustomer(), request);

            // 3. Modificamos el estado usando los getters estándar de tu ScoringResponse y tus enums reales
            if (scoring.getAutomaticallyDenied()) {
                request.setState(RequestStatus.DENIED);
            } else if (scoring.getAutomaticallyApproved()) {
                request.setState(RequestStatus.APPROVED); // Cambiado a APPROVED para que coincida con tu Enum
            } else {
                request.setState(RequestStatus.PENDING_ANALYST); // Manda a la bandeja del analista humano
            }

            // Guardamos el estado definitivo de la solicitud
            request = requestRepository.save(request);
        }

        return mapper.toDTO(request);
    }

    @Override
    public void logicalDelete(Long requestId) {
        Request request = requestRepository
                .findByIdActive(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validator.validateDeletion(request);
        request.setIsActive(0);
        requestRepository.save(request);
    }

    @Override
    public List<RequestResponseDTO> getPendingRequests() {
        return requestRepository
                .findByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public RequestResponseDTO resolveRequest(Long requestId, ResolveRequestDTO dto) {
        Request request = requestRepository
                .findByIdActive(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        validator.validateAnalystResolution(request);
        request.setState(dto.getStatus());
        request.setResolutionDate(LocalDateTime.now());

        Request updatedRequest = requestRepository.save(request);
        return mapper.toDTO(updatedRequest);
    }

    @Override
    public List<RequestResponseDTO> getAllRequests() {
        return requestRepository.findByIsActive(1)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }
}