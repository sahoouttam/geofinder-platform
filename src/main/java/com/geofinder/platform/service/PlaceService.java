package com.geofinder.platform.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import com.geofinder.platform.dto.request.CreatePlaceRequest;
import com.geofinder.platform.dto.request.OperatingHoursRequest;
import com.geofinder.platform.dto.request.TagUpdateRequest;
import com.geofinder.platform.dto.response.NearbyPlace;
import com.geofinder.platform.dto.response.PlaceResponse;
import com.geofinder.platform.dto.response.PlaceSummaryResponse;
import com.geofinder.platform.entity.OperatingHours;
import com.geofinder.platform.entity.Place;
import com.geofinder.platform.entity.PlaceType;
import com.geofinder.platform.exception.PlaceNotFoundException;
import com.geofinder.platform.exception.PlaceUpdateException;
import com.geofinder.platform.exception.ResourceNotFoundException;
import com.geofinder.platform.repository.PlaceRepository;

@Service
public class PlaceService {
    
    private final PlaceRepository placeRepository;

    @Autowired
    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public PlaceResponse createPlace(CreatePlaceRequest createPlaceRequest) {
        Place place = Place.builder()
                        .name(createPlaceRequest.getName())
                        .placeType(createPlaceRequest.getPlaceType())
                        .tags(createPlaceRequest.getTags())
                        .description(createPlaceRequest.getDescription())
                        .location(new GeoJsonPoint(createPlaceRequest.getLongitude(), 
                                createPlaceRequest.getLatitude()))
                        .address(createPlaceRequest.getAddress())
                        .operatingHours(toOperatingHours(createPlaceRequest
                                .getOperatingHours()))  
                        .attributes(createPlaceRequest.getAttributes())
                        .parentPlaceId(createPlaceRequest.getParentPlaceId())
                        .deliveryAvailable(createPlaceRequest.getPlaceType().supportsDelivery())
                        .build();
        Place placeCreated = placeRepository.save(place);
        return PlaceResponse.from(placeCreated);
    }

    public PlaceResponse updatePlaceTags(String id, TagUpdateRequest tagUpdateRequest) {
        boolean validUpdate = placeRepository.updatePlaceTags(id, tagUpdateRequest);
        if (!validUpdate) {
            throw new PlaceUpdateException("Failed to update place " + id);
        }
        Place place = findPlace(id);
        return PlaceResponse.from(place);
    }

    public PlaceResponse updatePlaceRatings(String id, double rating) {
        boolean validUpdate = placeRepository.updateRating(id, rating);
        if (!validUpdate) {
            throw new PlaceUpdateException("Failed to update place " + id);
        }
        Place place = findPlace(id);
        return PlaceResponse.from(place);
    }

    public List<PlaceSummaryResponse> findNearbyPlaces(double latitude, double longitude, 
                            double radiusKm, PlaceType placeType, int limit) {
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(longitude, latitude); 
        return placeRepository.findNearby(geoJsonPoint, radiusKm, placeType, limit);      
    }

    public Place findPlace(String id) {
        return placeRepository.findById(id)
                    .orElseThrow(() -> new PlaceNotFoundException(id));
    }

    private List<OperatingHours> toOperatingHours(List<OperatingHoursRequest> operatingHours) {
        if (operatingHours == null || operatingHours.isEmpty()) {
            return Collections.emptyList();
        }
        return operatingHours.stream()
                .map(operatingHour -> {
                    if (operatingHour.isClosed()) {
                        return OperatingHours.closedOn(operatingHour.getDayOfWeek());
                    }
                    if (operatingHour.getOpenTime() == null || 
                                    operatingHour.getCloseTime() == null) {
                        throw new ResourceNotFoundException(
                            "openTime and closeTime are required for " + 
                                    operatingHour.getDayOfWeek());
                    }
                    return OperatingHours.of(operatingHour.getDayOfWeek(), 
                            operatingHour.getOpenTime(), operatingHour.getCloseTime());
                })
                .collect(Collectors.toList());
    }

    public List<NearbyPlace> findNearby(double latitude, double longitude, double radiusKm, int limit) {
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(longitude, latitude);
        return placeRepository.findNearby(geoJsonPoint, radiusKm, limit)
                                    .getContent()
                                    .stream()
                                    .map(NearbyPlace::from)
                                    .collect(Collectors.toList());
    }
}
