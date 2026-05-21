package com.renting.backend.repositories;

import com.renting.backend.entities.Request;
import com.renting.backend.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    long countByCustomerIdAndStateAndCreatedAtGreaterThanEqual
            (Long customerId, RequestStatus state, LocalDateTime date);

    long countByIsActive(Integer isActive);

    long countByStateAndIsActive(RequestStatus state, Integer isActive);

    @Query("""
        SELECT r
        FROM Request r
        WHERE r.id = :id
        AND r.isActive = 1
    """)
    Optional<Request> findByIdActive(@Param("id") Long id);
}
