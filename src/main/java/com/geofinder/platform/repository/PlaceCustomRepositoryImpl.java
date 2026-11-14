package com.geofinder.platform.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators.Timezone;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Divide;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Multiply;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

    private final MongoTemplate mongoTemplate;

    private static final Timezone IST = Timezone.valueOf("Asia/Kolkata");

    public PlaceCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<PlaceSummaryResponse> findNearby(GeoJsonPoint geoJsonPoint, double radiusKm, 
                    PlaceType placeType, LocalDateTime openAt, int limit) {
        NearQuery nearQuery = NearQuery.near(geoJsonPoint, Metrics.KILOMETERS)
                    .maxDistance(new Distance(radiusKm, Metrics.KILOMETERS))
                    .spherical(true); 
        if (placeType != null) {
            Criteria criteria = Criteria.where("placeType").is(placeType);
            nearQuery.query(Query.query(criteria));
        }
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(geoNear(nearQuery, "distanceKm"));
        if (openAt != null) operations.add(match(openAtCriteria(openAt)));
        operations.add(limit(limit));
        operations.add(project("name", "placeType", "tags", "address",
                    "rating", "ratingCount", "deliveryAvailable", "distanceKm")
                .and(ArrayOperators.ArrayElemAt.arrayOf("location.coordinates")
                        .elementAt(1)).as("latitude")
                .and(ArrayOperators.ArrayElemAt.arrayOf("location.coordinates")
                        .elementAt(0)).as("longitude"));
        return mongoTemplate.aggregate(newAggregation(operations), Place.class, 
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

    @Override
    public List<Place> findByOpenNow(LocalDateTime openAt) {
        Criteria criteria = openAtCriteria(openAt);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Place.class);
    }

    @Override
    public List<Place> findByText(String text, int limit) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(text);
        TextQuery textQuery = TextQuery.queryText(textCriteria).sortByScore();
        textQuery.limit(limit);
        return mongoTemplate.find(textQuery, Place.class);
    }

    private Criteria openAtCriteria(LocalDateTime openAt) {
        String today = openAt.getDayOfWeek().name();
        String yesterday = openAt.minusDays(1).getDayOfWeek().name();
        String time = openAt.format(DateTimeFormatter.ofPattern("HH:mm"));

        AggregationExpression openedToday = BooleanOperators.And.and(
            ComparisonOperators.valueOf("h.closed").equalToValue(false),
            ComparisonOperators.valueOf("h.dayOfWeek").equalToValue(today),
            ComparisonOperators.valueOf(hhmm("h.openTime")).lessThanEqualToValue(time),
            BooleanOperators.Or.or(
                    ComparisonOperators.valueOf("h.overnight").equalToValue(true),
                    ComparisonOperators.valueOf(hhmm("h.closeTime")).greaterThanValue(time)));

        AggregationExpression spillover = BooleanOperators.And.and(
            ComparisonOperators.valueOf("h.closed").equalToValue(false),
            ComparisonOperators.valueOf("h.dayOfWeek").equalToValue(yesterday),
            ComparisonOperators.valueOf("h.overnight").equalToValue(true),
            ComparisonOperators.valueOf(hhmm("h.closeTime")).greaterThanValue(time));

        return Criteria.expr(
                    BooleanOperators.Or.or(hasShift(openedToday), 
                    hasShift(spillover)));
    }

    private AggregationExpression hhmm(String field) {
        return DateOperators.dateOf(field).toString("%H:%M").withTimezone(IST);
    }

    private AggregationExpression hasShift(AggregationExpression condition) {
        return ComparisonOperators.valueOf(
            ArrayOperators.Size.lengthOfArray(
                    ArrayOperators.Filter.filter("operatingHours").as("h").by(condition)))
            .greaterThanValue(0);
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
