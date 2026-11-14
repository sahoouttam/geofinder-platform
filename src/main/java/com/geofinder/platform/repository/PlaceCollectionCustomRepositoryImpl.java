package com.geofinder.platform.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.geofinder.platform.entity.PlaceCollection;

public class PlaceCollectionCustomRepositoryImpl implements PlaceCollectionCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PlaceCollectionCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<PlaceCollection> addPlaceIdToCollection(String id, String placeId) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().addToSet("placeIds", placeId)
                                    .currentDate("updatedAt");
        PlaceCollection updatedCollection = mongoTemplate.findAndModify(
                                    query, update,
                                    FindAndModifyOptions.options().returnNew(true),
                                    PlaceCollection.class);
        return Optional.ofNullable(updatedCollection);
    }

    @Override
    public Optional<PlaceCollection> removePlaceIdFromCollection(String id, String placeId) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().pull("placeIds", placeId)
                                    .currentDate("updatedAt");
        PlaceCollection updatedCollection = mongoTemplate.findAndModify(
                                    query, update,
                                    FindAndModifyOptions.options().returnNew(true),
                                    PlaceCollection.class);
        return Optional.ofNullable(updatedCollection);
    }
    
}
