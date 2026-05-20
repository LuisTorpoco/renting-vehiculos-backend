package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.DashboardStatsResponse;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.repositories.VehicleRepository;
import com.renting.backend.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final RequestRepository requestRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalRequests = requestRepository.countByIsActive(1);
        
        long approvedRequests = requestRepository.countByStateAndIsActive(RequestStatus.APPROVED, 1)
                + requestRepository.countByStateAndIsActive(RequestStatus.APPROVED_WITH_WARRANTIES, 1);
        
        long deniedRequests = requestRepository.countByStateAndIsActive(RequestStatus.DENIED, 1);
        
        long pendingRequests = requestRepository.countByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1);
        
        long totalCustomers = customerRepository.countByIsActive(1);
        
        long totalVehicles = vehicleRepository.count();
        
        long availableVehicles = vehicleRepository.countByAvailable(1);

        return DashboardStatsResponse.builder()
                .totalRequests(totalRequests)
                .approvedRequests(approvedRequests)
                .deniedRequests(deniedRequests)
                .pendingRequests(pendingRequests)
                .totalCustomers(totalCustomers)
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .build();
    }
}
