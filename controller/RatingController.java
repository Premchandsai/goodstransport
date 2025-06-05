package com.p2p.transport.controller;

import com.p2p.transport.model.DriverRating;
import com.p2p.transport.model.UserRatingAndReview;
import com.p2p.transport.response.ErrorDetail;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Operation(summary = "Submit user rating for driver", description = "Sender submits a rating for a driver after payment.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid rating"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/user")
    public ResponseEntity<ApiResponse<UserRatingAndReview>> submitUserRating(
            @RequestBody UserRatingAndReview rating) {
        try {
            ApiResponse<UserRatingAndReview> response = ratingService.submitUserRating(rating);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        } catch (Exception e) {
            ApiResponse<UserRatingAndReview> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid rating", null);
            response.setErrors(List.of(new ErrorDetail("INVALID_RATING", e.getMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Submit driver rating for user", description = "Driver submits a rating for a sender after payment.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rating submitted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid rating"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/driver")
    public ResponseEntity<ApiResponse<DriverRating>> submitDriverRating(
            @RequestBody DriverRating rating) {
        try {
            ApiResponse<DriverRating> response = ratingService.submitDriverRating(rating);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        } catch (Exception e) {
            ApiResponse<DriverRating> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid rating", null);
            response.setErrors(List.of(new ErrorDetail("INVALID_RATING", e.getMessage())));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get average rating for a user", description = "Retrieve the average rating for a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/user/{userId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageUserRating(@PathVariable UUID userId) {
        Double averageRating = ratingService.getAverageUserRating(userId);
        ApiResponse<Double> response = new ApiResponse<>(HttpStatus.OK.value(), "Average rating retrieved successfully", averageRating);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get average rating for a driver", description = "Retrieve the average rating for a driver.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/driver/{driverId}/average")
    public ResponseEntity<ApiResponse<Double>> getAverageDriverRating(@PathVariable UUID driverId) {
        Double averageRating = ratingService.getAverageDriverRating(driverId);
        ApiResponse<Double> response = new ApiResponse<>(HttpStatus.OK.value(), "Average rating retrieved successfully", averageRating);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}