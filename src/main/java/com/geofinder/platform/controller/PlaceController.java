package com.geofinder.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geofinder.platform.dto.request.CreatePlaceRequest;
import com.geofinder.platform.dto.request.TagUpdateRequest;
import com.geofinder.platform.dto.response.PlaceResponse;
import com.geofinder.platform.dto.response.PlaceSummaryResponse;
import com.geofinder.platform.entity.PlaceType;
import com.geofinder.platform.service.PlaceService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@RestController
@RequestMapping("/api/places")
public class PlaceController {
    
    private final PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> create(@RequestBody CreatePlaceRequest createPlaceRequest) {
        PlaceResponse placeResponse = placeService.createPlace(createPlaceRequest);
        return new ResponseEntity<>(placeResponse, HttpStatus.CREATED);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<PlaceSummaryResponse>> nearBy(@RequestParam double latitude,
                @RequestParam double longitude, @RequestParam double radiusKm, 
                @RequestParam PlaceType placeType, @RequestParam int limit) {
        List<PlaceSummaryResponse> summaryResponses = placeService
                .findNearbyPlaces(latitude, longitude, radiusKm, placeType, limit);
        return new ResponseEntity<>(summaryResponses, HttpStatus.OK);
    }

    @PatchMapping("/{id}/tags")
    public ResponseEntity<PlaceResponse> updateTags(@PathVariable String id, 
                                @RequestBody TagUpdateRequest tagUpdateRequest) {
        PlaceResponse placeResponse = placeService.updatePlaceTags(id, tagUpdateRequest);
        return new ResponseEntity<>(placeResponse, HttpStatus.OK);
    }

    @PatchMapping("/{id}/ratings")
    public ResponseEntity<PlaceResponse> updateRating(@PathVariable String id, 
            @RequestParam @DecimalMin("1") @DecimalMax("5") double rating) {
        PlaceResponse placeResponse = placeService.updatePlaceRatings(id, rating);
        return new ResponseEntity<>(placeResponse, HttpStatus.OK);
    }
}
