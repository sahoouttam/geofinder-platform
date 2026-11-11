package com.geofinder.platform.repository;

import java.util.List;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import com.geofinder.platform.entity.DeliveryZone;

public interface DeliveryZoneCustomRepository {
    
    List<DeliveryZone> findByPlaceIdAndPoint(String placedId, GeoJsonPoint geoJsonPoint);
}
