package com.geofinder.platform.repository;

import java.util.Optional;

import com.geofinder.platform.entity.PlaceCollection;

public interface PlaceCollectionCustomRepository {
    
    Optional<PlaceCollection> addPlaceIdToCollection(String id, String placeId);

    Optional<PlaceCollection> removePlaceIdFromCollection(String id, String placeId);
}
