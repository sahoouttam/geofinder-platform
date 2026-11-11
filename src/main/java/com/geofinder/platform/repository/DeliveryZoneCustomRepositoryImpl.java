package com.geofinder.platform.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.geofinder.platform.entity.DeliveryZone;

public class DeliveryZoneCustomRepositoryImpl implements DeliveryZoneCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DeliveryZoneCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DeliveryZone> findByPlaceIdAndPoint(String placedId, GeoJsonPoint geoJsonPoint) {
        Criteria criteria = Criteria.where("placeId").is(placedId)
                                    .and("area").intersects(geoJsonPoint);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, DeliveryZone.class);
    }
    
}
