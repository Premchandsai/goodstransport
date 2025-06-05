package com.p2p.transport.service;

import com.p2p.transport.model.Driver;
import com.p2p.transport.model.User;
import com.p2p.transport.model.enums.Role;
import com.p2p.transport.repository.DriverRepository;
import com.p2p.transport.repository.UserRepository;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository,
                         NotificationService notificationService) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ApiResponse<Driver> createDriver(Driver driver) {
        try {
            User user = userRepository.findById(driver.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getRole() != Role.DRIVER) {
                return new ApiResponse<>(400, "User must have DRIVER role", null,
                        List.of(new ErrorDetail("INVALID_ROLE", "User role is not DRIVER")));
            }

            if (driverRepository.existsByUserId(user.getId())) {
                return new ApiResponse<>(400, "Driver already exists for this user", null,
                        List.of(new ErrorDetail("DUPLICATE_DRIVER", "Driver profile already exists")));
            }

            driver.setId(UUID.randomUUID());
            Driver savedDriver = driverRepository.save(driver);
            notificationService.sendNotification(user.getId(), "Driver profile created successfully");
            return new ApiResponse<>(200, "Driver created successfully", savedDriver);
        } catch (RuntimeException e) {
            return new ApiResponse<>(400, e.getMessage(), null,
                    List.of(new ErrorDetail("ERROR", e.getMessage())));
        }
    }

    public ApiResponse<Driver> getDriverById(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        return new ApiResponse<>(200, "Driver retrieved successfully", driver);
    }

    public ApiResponse<List<Driver>> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAll();
        return new ApiResponse<>(200, "Drivers retrieved successfully", drivers);
    }

    @Transactional
    public ApiResponse<Driver> updateDriver(UUID driverId, Driver updatedDriver) {
        try {
            Driver existingDriver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            existingDriver.setVehicleType(updatedDriver.getVehicleType());
            existingDriver.setVehicleId(updatedDriver.getVehicleId());
            existingDriver.setLicenseNumber(updatedDriver.getLicenseNumber());

            Driver savedDriver = driverRepository.save(existingDriver);
            notificationService.sendNotification(existingDriver.getUser().getId(), "Driver profile updated successfully");
            return new ApiResponse<>(200, "Driver updated successfully", savedDriver);
        } catch (RuntimeException e) {
            return new ApiResponse<>(400, e.getMessage(), null,
                    List.of(new ErrorDetail("ERROR", e.getMessage())));
        }
    }

    @Transactional
    public ApiResponse<Void> deleteDriver(UUID driverId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            driverRepository.delete(driver);
            notificationService.sendNotification(driver.getUser().getId(), "Driver profile deleted");
            return new ApiResponse<>(200, "Driver deleted successfully", null);
        } catch (RuntimeException e) {
            return new ApiResponse<>(400, e.getMessage(), null,
                    List.of(new ErrorDetail("ERROR", e.getMessage())));
        }
    }
}