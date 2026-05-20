package com.renting.backend.controllers;

import com.renting.backend.dtos.request.*;
import com.renting.backend.dtos.response.*;
import com.renting.backend.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}