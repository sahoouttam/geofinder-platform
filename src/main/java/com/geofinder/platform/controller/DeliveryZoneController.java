package com.geofinder.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geofinder.platform.dto.request.CreateDeliveryZoneRequest;
import com.geofinder.platform.dto.response.DeliveryZoneResponse;
import com.geofinder.platform.service.DeliveryZoneService;


@RestController
@RequestMapping("/api/zones")
public class DeliveryZoneController {
    
    private final DeliveryZoneService deliveryZoneService;

    @Autowired
    public DeliveryZoneController(DeliveryZoneService deliveryZoneService) {
        this.deliveryZoneService = deliveryZoneService;
    }

    @PostMapping
    public ResponseEntity<DeliveryZoneResponse> addDeliveryZone(
                    @RequestBody CreateDeliveryZoneRequest createDeliveryZoneRequest) {
        DeliveryZoneResponse deliveryZoneResponse = deliveryZoneService
                        .createDeliveryZone(createDeliveryZoneRequest);
        return new ResponseEntity<>(deliveryZoneResponse, HttpStatus.CREATED);
    }

    @GetMapping("/deliverable")
    public ResponseEntity<List<DeliveryZoneResponse>> findByPlace(@RequestParam String placeId, 
                    @RequestParam double latitude, @RequestParam double longitude) {
        List<DeliveryZoneResponse> deliveryZoneResponses = deliveryZoneService
                            .findByPlaceIdAndPoint(placeId, latitude, longitude);
        return new ResponseEntity<>(deliveryZoneResponses, HttpStatus.OK);
    }
}
