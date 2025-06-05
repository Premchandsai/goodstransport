package com.p2p.transport.service;

import com.p2p.transport.model.*;
import com.p2p.transport.model.enums.PaymentStatusEnum;
import com.p2p.transport.repository.*;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RatingService {

    private final UserRatingRepository userRatingRepository;
    private final DriverRatingRepository driverRatingRepository;
    private final UserAggregatedRatingRepository userAggregatedRatingRepository;
    private final DriverAggregatedRatingRepository driverAggregatedRatingRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final NotificationService notificationService;

    public RatingService(UserRatingRepository userRatingRepository,
                         DriverRatingRepository driverRatingRepository,
                         UserAggregatedRatingRepository userAggregatedRatingRepository,
                         DriverAggregatedRatingRepository driverAggregatedRatingRepository,
                         PaymentRepository paymentRepository,
                         UserRepository userRepository,
                         DriverRepository driverRepository,
                         RideRepository rideRepository,
                         NotificationService notificationService) {
        this.userRatingRepository = userRatingRepository;
        this.driverRatingRepository = driverRatingRepository;
        this.userAggregatedRatingRepository = userAggregatedRatingRepository;
        this.driverAggregatedRatingRepository = driverAggregatedRatingRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApiResponse<UserRatingAndReview> submitUserRating(UserRatingAndReview rating) {
        try {
            validateRating(rating.getRide().getId(), rating.getReviewerUser().getId(), rating.getPayment().getPaymentId());
            UserRatingAndReview savedRating = userRatingRepository.save(rating);
            updateDriverAggregatedRating(rating.getDriver().getId());
            notificationService.sendNotification(rating.getDriver().getUser().getId(),
                    "You received a rating of " + rating.getRating() + " from user " + rating.getReviewerUser().getId());
            return new ApiResponse<>(200, "Rating submitted successfully", savedRating);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Invalid rating: " + e.getMessage(), null,
                    List.of(new ErrorDetail("INVALID_RATING", e.getMessage())));
        }
    }

    @Transactional
    public ApiResponse<DriverRating> submitDriverRating(DriverRating rating) {
        try {
            validateDriverRating(rating.getRide().getId(), rating.getReviewerDriver().getId(), rating.getPayment().getPaymentId());
            DriverRating savedRating = driverRatingRepository.save(rating);
            updateUserAggregatedRating(rating.getReviewedUser().getId());
            notificationService.sendNotification(rating.getReviewedUser().getId(),
                    "You received a rating of " + rating.getRating() + " from driver " + rating.getReviewerDriver().getId());
            return new ApiResponse<>(200, "Rating submitted successfully", savedRating);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Invalid rating: " + e.getMessage(), null,
                    List.of(new ErrorDetail("INVALID_RATING", e.getMessage())));
        }
    }

    private void validateRating(UUID rideId, UUID userId, UUID paymentId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatusEnum.COMPLETED) {
            throw new RuntimeException("Payment must be completed before rating");
        }
        if (!payment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Only the payment user can rate");
        }
        if (userRatingRepository.existsByRideIdAndReviewerUserId(rideId, userId)) {
            throw new RuntimeException("User has already rated this ride");
        }
    }

    private void validateDriverRating(UUID rideId, UUID driverId, UUID paymentId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatusEnum.COMPLETED) {
            throw new RuntimeException("Payment must be completed before rating");
        }
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Only the ride driver can rate");
        }
        if (driverRatingRepository.existsByRideIdAndReviewerDriverId(rideId, driverId)) {
            throw new RuntimeException("Driver has already rated this ride");
        }
    }

    private void updateUserAggregatedRating(UUID userId) {
        Double avgRating = driverRatingRepository.findAverageRatingByReviewedUserId(userId);
        int count = driverRatingRepository.countByReviewedUserId(userId);
        UserAggregatedRating aggregated = userAggregatedRatingRepository.findByUserId(userId)
                .orElse(UserAggregatedRating.builder()
                        .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                        .build());
        aggregated.setAverageRating(avgRating != null ? avgRating : 0.0);
        aggregated.setTotalRatingsCount(count);
        userAggregatedRatingRepository.save(aggregated);
    }

    private void updateDriverAggregatedRating(UUID driverId) {
        Double avgRating = userRatingRepository.findAverageRatingByReviewedDriverId(driverId);
        int count = userRatingRepository.countByReviewedDriverId(driverId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        DriverAggregatedRating aggregated = driverAggregatedRatingRepository.findByDriverId(driverId)
                .orElseGet(() -> DriverAggregatedRating.builder()
                        .driver(driver) // Pass the Driver object, not UUID
                        .build());
        aggregated.setAverageRating(avgRating != null ? avgRating : 0.0);
        aggregated.setTotalRatingsCount(count);
        driverAggregatedRatingRepository.save(aggregated);
    }

    public Double getAverageUserRating(UUID userId) {
        return userAggregatedRatingRepository.findByUserId(userId)
                .map(UserAggregatedRating::getAverageRating)
                .orElse(0.0);
    }

    public Double getAverageDriverRating(UUID driverId) {
        return driverAggregatedRatingRepository.findByDriverId(driverId)
                .map(DriverAggregatedRating::getAverageRating)
                .orElse(0.0);
    }

    public List<UserRatingAndReview> getUserRatings(UUID userId) {
        return userRatingRepository.findByReviewerUserId(userId);
    }

    public List<DriverRating> getDriverRatings(UUID driverId) {
        return driverRatingRepository.findByReviewerDriverId(driverId);
    }
}