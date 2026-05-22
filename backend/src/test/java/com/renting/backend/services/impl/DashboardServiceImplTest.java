package com.renting.backend.services.impl;

import com.renting.backend.dtos.response.DashboardStatsResponse;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.repositories.CustomerRepository;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.repositories.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para DashboardServiceImpl")
class DashboardServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    @Nested
    @DisplayName("Supuesto 1: Flujo Feliz (Caso Exitoso Estándar)")
    class HappyPathTests {

        @Test
        @DisplayName("Debería calcular y retornar las estadísticas correctamente cuando hay datos")
        void getDashboardStats_ShouldReturnCorrectStatistics() {
            // Arrange
            when(requestRepository.countByIsActive(1)).thenReturn(10L);
            when(requestRepository.countByStateAndIsActive(RequestStatus.APPROVED, 1)).thenReturn(4L);
            when(requestRepository.countByStateAndIsActive(RequestStatus.APPROVED_WITH_WARRANTIES, 1)).thenReturn(2L);
            when(requestRepository.countByStateAndIsActive(RequestStatus.DENIED, 1)).thenReturn(1L);
            when(requestRepository.countByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1)).thenReturn(3L);
            when(customerRepository.countByIsActive(1)).thenReturn(50L);
            when(vehicleRepository.count()).thenReturn(20L);
            when(vehicleRepository.countByAvailable(1)).thenReturn(15L);

            // Act
            DashboardStatsResponse response = dashboardService.getDashboardStats();

            // Assert
            assertNotNull(response);
            assertEquals(10L, response.getTotalRequests());
            assertEquals(6L, response.getApprovedRequests()); // 4 + 2 = 6
            assertEquals(1L, response.getDeniedRequests());
            assertEquals(3L, response.getPendingRequests());
            assertEquals(50L, response.getTotalCustomers());
            assertEquals(20L, response.getTotalVehicles());
            assertEquals(15L, response.getAvailableVehicles());
        }
    }

    @Nested
    @DisplayName("Supuesto 2: Base de Datos Vacía (Valores en Cero)")
    class EmptyDataTests {

        @Test
        @DisplayName("Debería retornar todas las métricas en cero si los repositorios devuelven cero")
        void getDashboardStats_ShouldReturnZeros_WhenNoDataExists() {
            // Arrange
            when(requestRepository.countByIsActive(1)).thenReturn(0L);
            when(requestRepository.countByStateAndIsActive(any(RequestStatus.class), eq(1))).thenReturn(0L);
            when(customerRepository.countByIsActive(1)).thenReturn(0L);
            when(vehicleRepository.count()).thenReturn(0L);
            when(vehicleRepository.countByAvailable(1)).thenReturn(0L);

            // Act
            DashboardStatsResponse response = dashboardService.getDashboardStats();

            // Assert
            assertNotNull(response);
            assertEquals(0L, response.getTotalRequests());
            assertEquals(0L, response.getApprovedRequests());
            assertEquals(0L, response.getDeniedRequests());
            assertEquals(0L, response.getPendingRequests());
            assertEquals(0L, response.getTotalCustomers());
            assertEquals(0L, response.getTotalVehicles());
            assertEquals(0L, response.getAvailableVehicles());
        }
    }

    @Nested
    @DisplayName("Supuesto 3: Manejo de Errores e Inconsistencias")
    class ErrorAndEdgeCaseTests {

        @Test
        @DisplayName("Debería lanzar una excepción si falla la conexión con la base de datos")
        void getDashboardStats_ShouldPropagateException_WhenRepositoryFails() {
            // Arrange: Simulamos que el primer repositorio se cae
            when(requestRepository.countByIsActive(1))
                    .thenThrow(new RuntimeException("Error de conexión con la base de datos"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                dashboardService.getDashboardStats();
            });

            assertEquals("Error de conexión con la base de datos", exception.getMessage());

            // Verificamos que tras el fallo no se intente llamar al resto de repositorios
            verifyNoInteractions(customerRepository);
            verifyNoInteractions(vehicleRepository);
        }
    }
}