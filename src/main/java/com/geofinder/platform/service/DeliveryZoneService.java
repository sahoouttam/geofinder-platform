package com.geofinder.platform.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Service;

import com.geofinder.platform.dto.request.CreateDeliveryZoneRequest;
import com.geofinder.platform.dto.response.DeliveryZoneResponse;
import com.geofinder.platform.entity.DeliveryZone;
import com.geofinder.platform.entity.Place;
import com.geofinder.platform.repository.DeliveryZoneRepository;


@Service
public class DeliveryZoneService {
    
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final PlaceService placeService;

    @Autowired
    public DeliveryZoneService(DeliveryZoneRepository deliveryZoneRepository, 
                                            PlaceService placeService) {
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.placeService = placeService;
    }

    public DeliveryZoneResponse createDeliveryZone(CreateDeliveryZoneRequest createDeliveryZoneRequest) {
        Place place = placeService.findPlace(createDeliveryZoneRequest.getPlaceId());
        if (!place.getPlaceType().supportsDelivery()) {
            throw new IllegalArgumentException("Place type does not support delivery");
        }
        List<Point> points = createDeliveryZoneRequest.getRings().stream()
                    .map(ring -> new Point(ring.get(0), ring.get(1)))
                    .collect(Collectors.toList());
        GeoJsonPolygon geoJsonPolygon;
        try {
            geoJsonPolygon = new GeoJsonPolygon(points);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Polygon ring must be closed (first point == last point)");
        }
        DeliveryZone deliveryZone = DeliveryZone.builder()
                    .placeId(createDeliveryZoneRequest.getPlaceId())
                    .area(geoJsonPolygon)
                    .minOrderValue(createDeliveryZoneRequest.getMinOrderValue())
                    .deliveryFee(createDeliveryZoneRequest.getDeliveryFee())
                    .build();
        DeliveryZone savedDeliveryZone = deliveryZoneRepository.save(deliveryZone);
        return DeliveryZoneResponse.from(savedDeliveryZone);
    }

    public List<DeliveryZoneResponse> findByPlaceIdAndPoint(String placeId, 
                                            double latitude, double longitude) {
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(new Point(longitude, latitude));
        List<DeliveryZone> deliveryZones =  deliveryZoneRepository
                            .findByPlaceIdAndPoint(placeId, geoJsonPoint);
        return deliveryZones.stream()
                    .map(DeliveryZoneResponse::from)
                    .collect(Collectors.toList());
    }
}
