package com.p2p.transport.controller;

import com.p2p.transport.model.TransportRequest;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.RequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransportRequest>> createRequest(@RequestBody TransportRequest request) {
        TransportRequest createdRequest = requestService.createRequest(request);
        ApiResponse<TransportRequest> response = new ApiResponse<>(HttpStatus.OK.value(), "Request created successfully", createdRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TransportRequest>> updateRequestStatus(@PathVariable UUID id, @RequestParam String status) {
        TransportRequest updatedRequest = requestService.updateRequestStatus(id, status);
        ApiResponse<TransportRequest> response = new ApiResponse<>(HttpStatus.OK.value(), "Request status updated successfully", updatedRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<ApiResponse<List<TransportRequest>>> getRequestsBySender(@PathVariable UUID senderId) {
        List<TransportRequest> requests = requestService.getRequestsBySender(senderId);
        ApiResponse<List<TransportRequest>> response = new ApiResponse<>(HttpStatus.OK.value(), "Sender requests retrieved successfully", requests);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<TransportRequest>>> getRequestsByDriver(@PathVariable UUID driverId) {
        List<TransportRequest> requests = requestService.getRequestsByDriver(driverId);
        ApiResponse<List<TransportRequest>> response = new ApiResponse<>(HttpStatus.OK.value(), "Driver requests retrieved successfully", requests);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}