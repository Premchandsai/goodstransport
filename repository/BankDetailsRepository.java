package com.p2p.transport.repository;

import com.p2p.transport.model.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankDetailsRepository extends KeyValueRepository<BankDetails, UUID> {
    Optional<BankDetails> findByUserId(UUID userId);
}
