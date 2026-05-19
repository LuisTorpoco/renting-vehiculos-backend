package com.renting.backend.controllers;

import com.renting.backend.dtos.request.CreateRequestDTO;
import com.renting.backend.dtos.request.ResolveRequestDTO;
import com.renting.backend.dtos.response.RequestResponseDTO;
import com.renting.backend.services.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestResponseDTO> createRequest(
            @RequestBody @Valid CreateRequestDTO dto
    ) {

        RequestResponseDTO response =
                requestService.createRequest(dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id
    ) {

        requestService.logicalDelete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<RequestResponseDTO>> getPendingRequests() {

        List<RequestResponseDTO> response =
                requestService.getPendingRequests();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<RequestResponseDTO> resolveRequest(
            @PathVariable Long id,
            @RequestBody @Valid ResolveRequestDTO dto
    ) {

        RequestResponseDTO response =
                requestService.resolveRequest(id, dto);

        return ResponseEntity.ok(response);
    }
}