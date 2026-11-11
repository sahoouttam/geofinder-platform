package com.geofinder.platform.repository;

import com.geofinder.platform.dto.request.LocationUpdateRequest;

public interface UserCustomRepository {
    
    boolean updateLocations(String id, LocationUpdateRequest locationUpdateRequest);
}
