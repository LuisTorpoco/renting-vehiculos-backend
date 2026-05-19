package com.renting.backend.repositories;


import com.renting.backend.entities.RequestDetail;
import com.renting.backend.entities.RequestDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestDetailRepository
        extends JpaRepository<RequestDetail, RequestDetailId> {
}
