package com.renting.backend.controllers;

import com.renting.backend.entities.Extra;
import com.renting.backend.repositories.ExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extras")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ExtraController {

    private final ExtraRepository
            extraRepository;

    @GetMapping
    public ResponseEntity<List<Extra>>
    getAllExtras() {

        List<Extra> extras =
                extraRepository.findAll();

        return ResponseEntity.ok(
                extras
        );
    }
}