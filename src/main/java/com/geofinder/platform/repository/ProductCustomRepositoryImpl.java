package com.geofinder.platform.repository;

import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import com.geofinder.platform.entity.Product;

public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    public ProductCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Product> findByText(String text, int limit) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(text);
        TextQuery textQuery = TextQuery.queryText(textCriteria).sortByScore();
        textQuery.addCriteria(Criteria.where("available").is(true));
        textQuery.limit(limit);
        return mongoTemplate.find(textQuery, Product.class);   
    }

    @Override
    public List<Product> findAllByTextAndPlaceIds(String text, List<String> placeIds) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(text);
        TextQuery textQuery = TextQuery.queryText(textCriteria).sortByScore();
        textQuery.addCriteria(Criteria.where("available").is(true));
        textQuery.addCriteria(Criteria.where("placeId").in(placeIds));
        return mongoTemplate.find(textQuery, Product.class);
    }
    
}
