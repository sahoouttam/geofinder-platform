package com.geofinder.platform.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateRequest {
    
    private CoordinateRequest home;

    private CoordinateRequest work;
}