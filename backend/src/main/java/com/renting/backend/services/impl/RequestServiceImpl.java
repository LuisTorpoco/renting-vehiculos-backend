package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.mapper.RequestMapper;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.RequestService;
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

    @Override
    public RequestResponseDTO createRequest(
            CreateRequestDTO dto
    ) {

        Request request = businessService.create(dto);

        return mapper.toDTO(request);
    }

    @Override
    public void logicalDelete(Long requestId) {

        Request request = requestRepository
                .findByIdActive(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found"
                        )
                );

        validator.validateDeletion(request);

        request.setIsActive(0);

        requestRepository.save(request);
    }

    @Override
    public List<RequestResponseDTO> getPendingRequests() {

        return requestRepository
                .findByStateAndIsActive(
                        RequestStatus.PENDING_ANALYST,
                        1
                )
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public RequestResponseDTO resolveRequest(
            Long requestId,
            ResolveRequestDTO dto
    ) {

        Request request = requestRepository
                .findByIdActive(requestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found"
                        )
                );

        validator.validateAnalystResolution(request);

        request.setState(dto.getStatus());

        request.setResolutionDate(LocalDateTime.now());

        Request updatedRequest =
                requestRepository.save(request);

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
