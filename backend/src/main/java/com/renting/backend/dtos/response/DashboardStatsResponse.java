package com.renting.backend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalRequests;
    private long approvedRequests;
    private long deniedRequests;
    private long pendingRequests;
    private long totalCustomers;
    private long totalVehicles;
    private long availableVehicles;
}
