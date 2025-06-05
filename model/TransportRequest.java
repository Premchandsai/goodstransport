package com.p2p.transport.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transport_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @Column(name = "goods_weight", nullable = false)
    private Double goodsWeight;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "cost", nullable = false, columnDefinition = "NUMERIC(12,2)")
    private BigDecimal cost; // Added to resolve getCost error
}