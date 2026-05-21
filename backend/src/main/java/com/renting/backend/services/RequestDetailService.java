package com.renting.backend.services;

import com.renting.backend.dtos.response.RequestWithDetailsResponseDTO;

public interface RequestDetailService {

    RequestWithDetailsResponseDTO getRequestWithDetails(Long requestId);
}
