package com.renting.backend.repositories;

import com.renting.backend.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    // Busca solo los coches que tengan AVAILABLE = 1 osea disponible
    List<Vehicle> findByAvailable(Integer available);

    long countByAvailable(Integer available);
}