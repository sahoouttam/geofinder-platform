package com.geofinder.platform.dto.response;

import java.util.List;

import com.geofinder.platform.entity.Address;
import com.geofinder.platform.entity.PlaceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSummaryResponse {
    
    private String id;
    private String name;
    private PlaceType placeType;
    private List<String> tags;
    private double latitude;
    private double longitude;
    private Address address;
    private double rating;
    private int ratingCount;
    private boolean deliveryAvailable;
    private double distanceKm;
}
