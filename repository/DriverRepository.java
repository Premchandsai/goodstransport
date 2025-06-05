package com.p2p.transport.repository;

import com.p2p.transport.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    boolean existsByUserId(UUID userId);
}