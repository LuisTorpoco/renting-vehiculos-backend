package com.renting.backend.services.impl;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import com.renting.backend.exception.ResourceNotFoundException;
import com.renting.backend.mapper.RequestMapper;
import com.renting.backend.repositories.RequestRepository;
import com.renting.backend.services.business.RequestBusinessService;
import com.renting.backend.validations.RequestValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para RequestServiceImpl")
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestBusinessService businessService;

    @Mock
    private RequestValidator validator;

    @Mock
    private RequestMapper mapper;

    @InjectMocks
    private RequestServiceImpl requestService;


    @Nested
    @DisplayName("Pruebas para createRequest")
    class CreateRequestTests {

        @Test
        @DisplayName("Debería crear la solicitud y retornar el DTO correctamente")
        void createRequest_HappyPath() {

            CreateRequestDTO dto = new CreateRequestDTO();
            Request mockRequest = new Request();


            RequestResponseDTO expectedResponse = RequestResponseDTO.builder()
                    .id(1L)
                    .status(RequestStatus.PENDING_ANALYST)
                    .build();

            when(businessService.create(dto)).thenReturn(mockRequest);
            when(mapper.toDTO(mockRequest)).thenReturn(expectedResponse);


            RequestResponseDTO actualResponse = requestService.createRequest(dto);


            assertNotNull(actualResponse);
            assertEquals(1L, actualResponse.getId());
            verify(businessService, times(1)).create(dto);
            verify(mapper, times(1)).toDTO(mockRequest);
        }
    }


    @Nested
    @DisplayName("Pruebas para logicalDelete")
    class LogicalDeleteTests {

        @Test
        @DisplayName("Debería marcar isActive = 0 cuando la solicitud existe y pasa la validación")
        void logicalDelete_HappyPath() {

            Long requestId = 1L;
            Request mockRequest = new Request();
            mockRequest.setIsActive(1);

            when(requestRepository.findByIdActive(requestId)).thenReturn(Optional.of(mockRequest));
            doNothing().when(validator).validateDeletion(mockRequest);
            when(requestRepository.save(mockRequest)).thenReturn(mockRequest);


            requestService.logicalDelete(requestId);


            assertEquals(0, mockRequest.getIsActive(), "El estado isActive debería cambiar a 0");
            verify(requestRepository, times(1)).findByIdActive(requestId);
            verify(validator, times(1)).validateDeletion(mockRequest);
            verify(requestRepository, times(1)).save(mockRequest);
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si la solicitud no existe o está inactiva")
        void logicalDelete_NotFound() {

            Long requestId = 1L;
            when(requestRepository.findByIdActive(requestId)).thenReturn(Optional.empty());


            assertThrows(ResourceNotFoundException.class, () -> requestService.logicalDelete(requestId));


            verify(validator, never()).validateDeletion(any());
            verify(requestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debería propagar excepción si el validador deniega la eliminación")
        void logicalDelete_ValidationFails() {

            Long requestId = 1L;
            Request mockRequest = new Request();
            when(requestRepository.findByIdActive(requestId)).thenReturn(Optional.of(mockRequest));

            doThrow(new RuntimeException("No se puede eliminar la solicitud"))
                    .when(validator).validateDeletion(mockRequest);


            assertThrows(RuntimeException.class, () -> requestService.logicalDelete(requestId));
            verify(requestRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("Pruebas para getPendingRequests")
    class GetPendingRequestsTests {

        @Test
        @DisplayName("Debería retornar una lista de DTOs cuando existen solicitudes pendientes")
        void getPendingRequests_WithData() {

            Request req1 = new Request();
            Request req2 = new Request();
            List<Request> mockList = List.of(req1, req2);

            RequestResponseDTO dto = RequestResponseDTO.builder().id(100L).build();

            when(requestRepository.findByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1)).thenReturn(mockList);
            when(mapper.toDTO(any(Request.class))).thenReturn(dto);


            List<RequestResponseDTO> result = requestService.getPendingRequests();


            assertNotNull(result);
            assertEquals(2, result.size());
            verify(requestRepository, times(1)).findByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1);
            verify(mapper, times(2)).toDTO(any(Request.class));
        }

        @Test
        @DisplayName("Debería retornar una lista vacía si no hay pendientes")
        void getPendingRequests_Empty() {

            when(requestRepository.findByStateAndIsActive(RequestStatus.PENDING_ANALYST, 1)).thenReturn(Collections.emptyList());


            List<RequestResponseDTO> result = requestService.getPendingRequests();


            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(mapper, never()).toDTO(any());
        }
    }


    @Nested
    @DisplayName("Pruebas para resolveRequest")
    class ResolveRequestTests {

        @Test
        @DisplayName("Debería actualizar el estado y fecha de resolución exitosamente")
        void resolveRequest_HappyPath() {

            Long requestId = 1L;
            ResolveRequestDTO dto = new ResolveRequestDTO();
            dto.setStatus(RequestStatus.APPROVED);

            Request mockRequest = new Request();
            mockRequest.setState(RequestStatus.PENDING_ANALYST);

            Request updatedRequest = new Request();


            RequestResponseDTO expectedResponse = RequestResponseDTO.builder()
                    .id(requestId)
                    .status(RequestStatus.APPROVED)
                    .build();

            when(requestRepository.findByIdActive(requestId)).thenReturn(Optional.of(mockRequest));
            doNothing().when(validator).validateAnalystResolution(mockRequest);
            when(requestRepository.save(mockRequest)).thenReturn(updatedRequest);
            when(mapper.toDTO(updatedRequest)).thenReturn(expectedResponse);

            RequestResponseDTO result = requestService.resolveRequest(requestId, dto);

      
            assertNotNull(result);
            assertEquals(RequestStatus.APPROVED, mockRequest.getState());
            assertNotNull(mockRequest.getResolutionDate(), "La fecha de resolución debió asignarse dinámicamente");
            verify(requestRepository, times(1)).save(mockRequest);
        }

        @Test
        @DisplayName("Debería lanzar ResourceNotFoundException si no existe al resolver")
        void resolveRequest_NotFound() {
       
            Long requestId = 1L;
            ResolveRequestDTO dto = new ResolveRequestDTO();
            when(requestRepository.findByIdActive(requestId)).thenReturn(Optional.empty());


            assertThrows(ResourceNotFoundException.class, () -> requestService.resolveRequest(requestId, dto));
            verify(validator, never()).validateAnalystResolution(any());
            verify(requestRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Pruebas para getAllRequests")
    class GetAllRequestsTests {

        @Test
        @DisplayName("Debería retornar todas las solicitudes activas mapeadas a DTO")
        void getAllRequests_HappyPath() {

            Request req = new Request();

            RequestResponseDTO expectedResponse = RequestResponseDTO.builder().id(5L).build();

            when(requestRepository.findByIsActive(1)).thenReturn(List.of(req));
            when(mapper.toDTO(req)).thenReturn(expectedResponse);


            List<RequestResponseDTO> result = requestService.getAllRequests();


            assertNotNull(result);
            assertEquals(1, result.size());
            verify(requestRepository, times(1)).findByIsActive(1);
        }
    }
}