package com.example.obcare.repository;

import com.example.obcare.model.LocationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationMessage, Long> {
    // JpaRepository već daje osnovne metode: save, findAll, findById, delete...
}
