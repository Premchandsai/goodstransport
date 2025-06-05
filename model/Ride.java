package com.p2p.transport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(name = "departure_location", nullable = false)
    private String departureLocation;

    @Column(name = "destination_location", nullable = false)
    private String destinationLocation;

    @Column(name = "available_space", nullable = false)
    private Double availableSpace;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "fare", nullable = false, columnDefinition = "NUMERIC(12,2)")
    private BigDecimal fare; // Added to resolve getFare error
}