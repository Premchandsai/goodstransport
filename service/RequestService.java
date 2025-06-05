package com.p2p.transport.service;

import com.p2p.transport.model.Ride;
import com.p2p.transport.model.TransportRequest;
import com.p2p.transport.repository.RideRepository;
import com.p2p.transport.repository.TransportRequestRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    private final TransportRequestRepository transportRequestRepository;
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public RequestService(TransportRequestRepository transportRequestRepository, RideRepository rideRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.transportRequestRepository = transportRequestRepository;
        this.rideRepository = rideRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public TransportRequest createRequest(TransportRequest request) {
        request.setStatus("PENDING");
        TransportRequest saved = transportRequestRepository.save(request);
        kafkaTemplate.send("transport-requests", "New request for driver " + request.getRide().getDriver().getId() + ": " + saved.getId());
        return saved;
    }

    public TransportRequest updateRequestStatus(UUID id, String status) {
        TransportRequest request = transportRequestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        TransportRequest updated = transportRequestRepository.save(request);
        if ("ACCEPTED".equals(status)) {
            Ride ride = rideRepository.findById(request.getRide().getId()).orElseThrow();
            ride.setStatus("ACCEPTED");
            rideRepository.save(ride);
        }
        kafkaTemplate.send("transport-requests", "Request " + id + " status: " + status);
        return updated;
    }

    public List<TransportRequest> getRequestsBySender(UUID senderId) {
        return transportRequestRepository.findBySenderId(senderId);
    }

    public List<TransportRequest> getRequestsByDriver(UUID driverId) {
        return transportRequestRepository.findByRideDriverId(driverId);
    }
}