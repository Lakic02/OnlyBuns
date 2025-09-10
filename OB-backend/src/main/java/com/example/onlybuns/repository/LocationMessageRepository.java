package com.example.onlybuns.repository;

import com.example.onlybuns.domain.LocationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationMessageRepository extends JpaRepository<LocationMessage, Long> {
    // Nema potrebe da dodaješ ništa – JpaRepository već ima findAll(), findById(), save(), itd.
}
