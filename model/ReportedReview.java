package com.p2p.transport.model;

import com.p2p.transport.model.enums.ReportedReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reported_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportedReview {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_rating_review_id", foreignKey = @ForeignKey(name = "fk_reported_user_review"))
    private UserRatingAndReview userRatingAndReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_rating_id", foreignKey = @ForeignKey(name = "fk_reported_driver_review"))
    private DriverRating driverRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reported_review_reported_by_user"))
    private User reportedByUser;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportedReviewStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ReportedReviewStatus.PENDING;
        }
        validateReviewReference();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        validateReviewReference();
    }

    private void validateReviewReference() {
        boolean hasUserReview = userRatingAndReview != null;
        boolean hasDriverReview = driverRating != null;
        if (hasUserReview == hasDriverReview) {
            throw new IllegalStateException("Exactly one of userRatingAndReview or driverRating must be set.");
        }
    }
}
