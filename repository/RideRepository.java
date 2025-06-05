package com.p2p.transport.repository;

import com.p2p.transport.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> findByDepartureLocationAndDestinationLocationAndDepartureTimeAfter(
            String departure, String destination, LocalDateTime time);
}