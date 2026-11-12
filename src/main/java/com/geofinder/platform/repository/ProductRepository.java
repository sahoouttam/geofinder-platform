package com.geofinder.platform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.geofinder.platform.entity.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductCustomRepository {
    
}
