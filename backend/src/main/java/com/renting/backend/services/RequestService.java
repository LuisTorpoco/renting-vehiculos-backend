package com.renting.backend.services;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;

import java.util.List;

public interface RequestService {

    RequestResponseDTO createRequest(CreateRequestDTO dto);

    void logicalDelete(Long requestId);

    List<RequestResponseDTO> getPendingRequests();
    List<RequestResponseDTO> getAllRequests();

    RequestResponseDTO resolveRequest(
            Long requestId,
            ResolveRequestDTO dto
    );
}
