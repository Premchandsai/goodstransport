package com.p2p.transport.model;

import com.p2p.transport.model.enums.TransactionStatus;
import com.p2p.transport.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "reference_id", nullable = false)
    private String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false, columnDefinition = "NUMERIC(12,2)")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
        this.referenceId = generateReferenceId();
    }

    private String generateReferenceId() {
        String datePart = LocalDateTime.now().toLocalDate().toString().replace("-", "");
        int randomPart = (int) (Math.random() * 1000000);
        return "TXN-" + datePart + "-" + randomPart;
    }
}
