package com.geofinder.platform.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.geofinder.platform.dto.request.CoordinateRequest;
import com.geofinder.platform.dto.request.LocationUpdateRequest;
import com.geofinder.platform.entity.User;
import com.mongodb.client.result.UpdateResult;

public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final MongoTemplate mongoTemplate;

    public UserCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean updateLocations(String id, LocationUpdateRequest locationUpdateRequest) {
        Query query = Query.query(Criteria.where("_id").is(id));
        CoordinateRequest home = locationUpdateRequest.getHome();
        CoordinateRequest work = locationUpdateRequest.getWork();
        if (home == null && work == null) {
            return mongoTemplate.exists(query, User.class);
        }
        Update update = new Update().currentDate("updatedAt");
        if (home != null) {
            double longitude = home.getLongitude();
            double latitude = home.getLatitude();
            update.set("homeLocation", new GeoJsonPoint(longitude, latitude));
        }
        if (work != null) {
            double longitude = work.getLongitude();
            double latitude = work.getLatitude();
            update.set("workLocation", new GeoJsonPoint(longitude, latitude));
        }
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
        return updateResult.getMatchedCount() > 0;
    }
    
}
