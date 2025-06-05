package com.p2p.transport.service;

import com.p2p.transport.model.Payment;
import com.p2p.transport.model.Ride;
import com.p2p.transport.model.TransportRequest;
import com.p2p.transport.model.User;
import com.p2p.transport.repository.PaymentRepository;
import com.p2p.transport.repository.RideRepository;
import com.p2p.transport.repository.TransportRequestRepository;
import com.p2p.transport.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final TransportRequestRepository transportRequestRepository;
    private final PaymentRepository paymentRepository;

    public AdminService(UserRepository userRepository, RideRepository rideRepository,
                        TransportRequestRepository transportRequestRepository,
                        PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.transportRequestRepository = transportRequestRepository;
        this.paymentRepository = paymentRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public List<TransportRequest> getAllRequests() {
        return transportRequestRepository.findAll();
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional
    public void banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public String generateReport() {
        long userCount = userRepository.count();
        long rideCount = rideRepository.count();
        long requestCount = transportRequestRepository.count();
        long paymentCount = paymentRepository.count();
        return String.format("Report: Users=%d, Rides=%d, Requests=%d, Payments=%d",
                userCount, rideCount, requestCount, paymentCount);
    }
}