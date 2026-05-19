package com.renting.backend.repositories;

import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository
        extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndIsActive(
            Long id,
            Integer isActive
    );

    List<Request> findByStateAndIsActive(
            RequestStatus status,
            Integer isActive
    );
}
