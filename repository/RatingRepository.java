package com.p2p.transport.repository;

import com.p2p.transport.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findByReviewedId(UUID reviewedId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.reviewed.id = :reviewedId")
    Double findAverageScoreByReviewedId(UUID reviewedId);

    boolean existsByRequestIdAndReviewerId(UUID requestId, UUID reviewerId);
}