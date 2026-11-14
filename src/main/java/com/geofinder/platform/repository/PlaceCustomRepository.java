package com.geofinder.platform.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.geofinder.platform.dto.request.TagUpdateRequest;
import com.geofinder.platform.dto.response.PlaceSummaryResponse;
import com.geofinder.platform.entity.Place;
import com.geofinder.platform.entity.PlaceType;

public interface PlaceCustomRepository {
    
    List<PlaceSummaryResponse> findNearby(GeoJsonPoint geoJsonPoint, double radiusKm, 
                    PlaceType placeType, LocalDateTime openAt, int limit);

    boolean updatePlaceTags(String id, TagUpdateRequest tagUpdateRequest);

    boolean updateRating(String id, double rating);

    GeoResults<Place> findNearby(GeoJsonPoint geoJsonPoint, double radiusKm, int limit);

    List<Place> findByOpenNow(LocalDateTime openAt);
}
