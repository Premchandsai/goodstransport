package com.p2p.transport.service;

import com.p2p.transport.model.Ride;
import com.p2p.transport.repository.RideRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public RideService(RideRepository rideRepository, RedisTemplate<String, Object> redisTemplate) {
        this.rideRepository = rideRepository;
        this.redisTemplate = redisTemplate;
    }

    public Ride createRide(Ride ride) {
        Ride saved = rideRepository.save(ride);
        redisTemplate.opsForValue().set("ride:" + saved.getId(), saved);
        return saved;
    }

    public List<Ride> searchRides(String departure, String destination, LocalDateTime time) {
        return rideRepository.findByDepartureLocationAndDestinationLocationAndDepartureTimeAfter(departure, destination, time);
    }

    public Ride updateRide(UUID id, Ride updatedRide) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (!"AVAILABLE".equals(ride.getStatus())) throw new RuntimeException("Cannot edit non-available ride");
        ride.setDepartureLocation(updatedRide.getDepartureLocation());
        ride.setDestinationLocation(updatedRide.getDestinationLocation());
        ride.setAvailableSpace(updatedRide.getAvailableSpace());
        ride.setDepartureTime(updatedRide.getDepartureTime());
        Ride saved = rideRepository.save(ride);
        redisTemplate.opsForValue().set("ride:" + saved.getId(), saved);
        return saved;
    }

    public void cancelRide(UUID id) {
        Ride ride = rideRepository.findById(id).orElseThrow(() -> new RuntimeException("Ride not found"));
        if (!"AVAILABLE".equals(ride.getStatus())) throw new RuntimeException("Cannot cancel non-available ride");
        rideRepository.deleteById(id);
        redisTemplate.delete("ride:" + id);
    }
}