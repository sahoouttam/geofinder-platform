package com.geofinder.platform.dto.response;

import org.springframework.data.geo.GeoResult;

import com.geofinder.platform.entity.Place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbyPlace {
    
    private String id; 
    
    private String name; 
    
    private double distanceKm;

    public static NearbyPlace from(GeoResult<Place> result) {
        return new NearbyPlace(
            result.getContent().getId(),
            result.getContent().getName(),
            result.getDistance().getValue()
        );
    }
}
