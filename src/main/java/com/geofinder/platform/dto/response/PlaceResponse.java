package com.geofinder.platform.dto.response;

import java.time.LocalDateTime;

import com.geofinder.platform.entity.Address;
import com.geofinder.platform.entity.Place;
import com.geofinder.platform.entity.PlaceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {
    
    private String id; 
    private String name; 
    private PlaceType placeType;
    private double latitude; 
    private double longitude; 
    private Address address;
    private double rating;
    private int ratingCount;
    private LocalDateTime createdAt;

    public static PlaceResponse from(Place place) {
        return PlaceResponse.builder()
                    .id(place.getId())
                    .name(place.getName())
                    .placeType(place.getPlaceType())
                    .latitude(place.getLocation().getY())
                    .longitude(place.getLocation().getX())
                    .address(place.getAddress())
                    .rating(place.getRating())
                    .ratingCount(place.getRatingCount())
                    .createdAt(place.getCreatedAt())
                    .build();
    }
}
