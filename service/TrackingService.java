package com.p2p.transport.service;

import com.p2p.transport.model.Ride;
import com.p2p.transport.model.Tracking;
import com.p2p.transport.repository.RideRepository;
import com.p2p.transport.repository.TrackingRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final RideRepository rideRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestTemplate restTemplate;

    public TrackingService(TrackingRepository trackingRepository, RideRepository rideRepository,
                           SimpMessagingTemplate messagingTemplate, RestTemplate restTemplate) {
        this.trackingRepository = trackingRepository;
        this.rideRepository = rideRepository;
        this.messagingTemplate = messagingTemplate;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public Tracking updateLocation(UUID rideId, double latitude, double longitude) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Tracking tracking = Tracking.builder()
                .ride(ride)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        tracking = trackingRepository.save(tracking);

        // Get address using Nominatim (OSM geocoding)
        String address = getAddressFromCoordinates(latitude, longitude);

        // Broadcast to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/tracking/" + rideId, new TrackingUpdate(
                rideId, latitude, longitude, address, tracking.getUpdatedAt()
        ));

        return tracking;
    }

    public Tracking getLatestLocation(UUID rideId) {
        return trackingRepository.findLatestByRideId(rideId)
                .orElseThrow(() -> new RuntimeException("No tracking data found for ride"));
    }

    private String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f&zoom=18&addressdetails=1", latitude, longitude);
            NominatimResponse response = restTemplate.getForObject(url, NominatimResponse.class);
            return response != null && response.getDisplayName() != null ? response.getDisplayName() : "Unknown address";
        } catch (Exception e) {
            return "Error retrieving address: " + e.getMessage();
        }
    }

    public record TrackingUpdate(UUID rideId, double latitude, double longitude, String address, LocalDateTime updatedAt) {}

    // Inner class for Nominatim response
    private static class NominatimResponse {
        private String display_name;

        public String getDisplayName() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }
    }
}