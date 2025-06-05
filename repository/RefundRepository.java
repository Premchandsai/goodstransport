package com.p2p.transport.repository;

import com.p2p.transport.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
}
