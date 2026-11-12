package com.geofinder.platform.repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.geofinder.platform.dto.request.TagUpdateRequest;
import com.geofinder.platform.dto.response.PlaceSummaryResponse;
import com.geofinder.platform.entity.Place;
import com.geofinder.platform.entity.PlaceType;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.geoNear;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final MongoTemplate mongoTemplate;

    public PlaceCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<PlaceSummaryResponse> findNearby(GeoJsonPoint geoJsonPoint, double radiusKm, 
                                    PlaceType placeType, int limit) {
        NearQuery nearQuery = NearQuery.near(geoJsonPoint)
                    .maxDistance(new Distance(radiusKm, Metrics.KILOMETERS))
                    .spherical(true); 
        if (placeType != null) {
            Criteria criteria = Criteria.where("placeType").is(placeType);
            nearQuery.query(Query.query(criteria));
        }
        Aggregation aggregation = newAggregation(
            geoNear(nearQuery, "distanceKm"),
            limit(limit),
            project("name", "placeType", "tags", "address",
                    "rating", "ratingCount", "deliveryAvailable", "distanceKm")
                .and(ArrayOperators.ArrayElemAt.arrayOf("location.coordinates")
                        .elementAt(1)).as("latitude")
                .and(ArrayOperators.ArrayElemAt.arrayOf("location.coordinates")
                        .elementAt(0)).as("longitude"));
        return mongoTemplate.aggregate(aggregation, Place.class, 
                        PlaceSummaryResponse.class).getMappedResults();
    }

    @Override
    public boolean updatePlaceTags(String id, TagUpdateRequest tagUpdateRequest) {
        Query query = Query.query(Criteria.where("_id").is(id));
        
        List<String> add = normalize(tagUpdateRequest.getAdd());
        List<String> remove = normalize(tagUpdateRequest.getRemove());

        if (add.isEmpty() && remove.isEmpty()) {
            return mongoTemplate.exists(query, Place.class);
        }

        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkMode.ORDERED, Place.class);
        if (!remove.isEmpty()) {
            Update update = new Update()
                                .pullAll("tags", remove.toArray())
                                .currentDate("updatedAt");
            bulkOperations.updateOne(query, update);
        }
        if (!add.isEmpty()) {
            Update update = new Update().addToSet("tags")
                                .each(add.toArray()).currentDate("updatedAt");
            bulkOperations.updateOne(query, update);
        }
        BulkWriteResult writeResult = bulkOperations.execute();
        return writeResult.getMatchedCount() > 0;
    }

    @Override
    public boolean updateRating(String id, double rating) {
        Query query = Query.query(Criteria.where("_id").is(id));
        AggregationUpdate aggregationUpdate = AggregationUpdate.update()
                    .set("rating").toValue(
                        Divide.valueOf(Add.valueOf(
                            Multiply.valueOf("rating").multiplyBy("ratingCount")).add(rating))
                                .divideBy(Add.valueOf("ratingCount").add(1)))  
                    .set("ratingCount").toValue(Add.valueOf("ratingCount").add(1))
                    .set("updatedAt").toValue(LocalDateTime.now());
        UpdateResult updateResult = mongoTemplate.updateFirst(
                                query, aggregationUpdate, Place.class);       
        return updateResult.getMatchedCount() > 0;   
    }

    @Override
    public GeoResults<Place> findNearby(GeoJsonPoint geoJsonPoint, double radiusKm, int limit) {
        NearQuery nearQuery = NearQuery.near(geoJsonPoint, Metrics.KILOMETERS)
                    .maxDistance(new Distance(radiusKm, Metrics.KILOMETERS))
                    .spherical(true)
                    .limit(limit);
        return mongoTemplate.geoNear(nearQuery, Place.class);
    }

    private List<String> normalize(List<String> tags) {
        if (tags == null || tags.isEmpty()) return Collections.emptyList();
        return tags.stream()
            .filter(Objects::nonNull)
            .map(t -> t.trim().toLowerCase())
            .filter(t -> !t.isEmpty())
            .distinct()
            .toList();
    }
}
