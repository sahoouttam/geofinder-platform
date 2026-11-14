package com.geofinder.platform.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geofinder.platform.dto.request.CreateCollectionRequest;
import com.geofinder.platform.dto.response.PlaceCollectionDetailResponse;
import com.geofinder.platform.dto.response.PlaceCollectionResponse;
import com.geofinder.platform.dto.response.PlaceResponse;
import com.geofinder.platform.entity.PlaceCollection;
import com.geofinder.platform.exception.PlaceCollectionNotFoundException;
import com.geofinder.platform.exception.PlaceNotFoundException;
import com.geofinder.platform.exception.UserNotFoundException;
import com.geofinder.platform.repository.PlaceCollectionRepository;

@Service
public class PlaceCollectionService {

    private final PlaceCollectionRepository placeCollectionRepository;
    private final UserService userService;
    private final PlaceService placeService;

    @Autowired
    public PlaceCollectionService(PlaceCollectionRepository placeCollectionRepository,
                                    UserService userService,
                                    PlaceService placeService) {
        this.placeCollectionRepository = placeCollectionRepository;
        this.userService = userService;
        this.placeService = placeService;
    }

    public PlaceCollectionResponse createCollection(CreateCollectionRequest collectionRequest) {
        if (!userService.exitsUser(collectionRequest.getUserId())) {
            throw new UserNotFoundException(collectionRequest.getUserId());
        }
        PlaceCollection placeCollection = PlaceCollection.builder()
                        .userId(collectionRequest.getUserId())
                        .name(collectionRequest.getName())
                        .build();
        PlaceCollection createdCollection = placeCollectionRepository.save(placeCollection);
        return PlaceCollectionResponse.from(createdCollection);
    }

    public PlaceCollectionResponse addToCollection(String id, String placeId) {
        if (!placeService.placeExists(placeId)) throw new PlaceNotFoundException(placeId);
        return placeCollectionRepository.addPlaceIdToCollection(id, placeId)
                        .map(PlaceCollectionResponse::from)
                        .orElseThrow(() -> new PlaceCollectionNotFoundException(id));
    }

    public PlaceCollectionResponse removeFromCollection(String id, String placeId) {
        return placeCollectionRepository.removePlaceIdFromCollection(id, placeId)
                        .map(PlaceCollectionResponse::from)
                        .orElseThrow(() -> new PlaceCollectionNotFoundException(id));
    }

    public PlaceCollectionDetailResponse getAllCollectionPlaces(String id) {
        PlaceCollection placeCollection = placeCollectionRepository.findById(id)
                        .orElseThrow(() -> new PlaceCollectionNotFoundException(id));
        List<PlaceResponse> placeResponses = placeService
                        .findAllPlacesByIds(placeCollection.getPlaceIds());
        return PlaceCollectionDetailResponse.from(placeCollection, placeResponses);
    }
    
}
