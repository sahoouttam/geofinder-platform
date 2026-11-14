package com.geofinder.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geofinder.platform.dto.request.CreateCollectionRequest;
import com.geofinder.platform.dto.response.PlaceCollectionDetailResponse;
import com.geofinder.platform.dto.response.PlaceCollectionResponse;
import com.geofinder.platform.service.PlaceCollectionService;

@RestController
@RequestMapping("/api/collections")
public class PlaceCollectionController {
    
    private final PlaceCollectionService collectionService;
    
    @Autowired
    public PlaceCollectionController(PlaceCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping
    public ResponseEntity<PlaceCollectionResponse> create(
                    @RequestBody CreateCollectionRequest createCollectionRequest) {
        PlaceCollectionResponse collectionResponse = collectionService
                                    .createCollection(createCollectionRequest);
        return new ResponseEntity<>(collectionResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/places/{placeId}")
    public ResponseEntity<PlaceCollectionResponse> addToCollection(
                                    @PathVariable String id, 
                                    @PathVariable String placeId) {
        PlaceCollectionResponse collectionResponse = collectionService
                                    .addToCollection(id, placeId);
        return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/places/{placeId}")
    public ResponseEntity<PlaceCollectionResponse> removeFromCollection(
                                    @PathVariable String id, 
                                    @PathVariable String placeId) {
        PlaceCollectionResponse collectionResponse = collectionService
                                    .removeFromCollection(id, placeId);
        return new ResponseEntity<>(collectionResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceCollectionDetailResponse> getAllCollectionPlaces(
                                    @PathVariable String id) {
        PlaceCollectionDetailResponse collectionDetailResponse = collectionService
                                        .getAllCollectionPlaces(id);
        return new ResponseEntity<>(collectionDetailResponse, HttpStatus.OK);
    }
}
