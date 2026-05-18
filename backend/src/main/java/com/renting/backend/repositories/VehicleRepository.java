package com.renting.backend.repositories;

import com.renting.backend.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

 //Repositorio para la gestión de datos de la entidad Vehicle en Oracle.

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    /**
     * Devuelve la lista de vehículos activos para el catalogo que vera el usuario.
     * Filtra automáticamente los que tengan un borrado logico (isActive = 0)
     */
    List<Vehicle> findByIsActive(Integer isActive);
}