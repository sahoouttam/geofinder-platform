package com.geofinder.platform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.geofinder.platform.entity.PlaceCollection;

@Repository
public interface PlaceCollectionRepository extends MongoRepository<PlaceCollection, String>, PlaceCollectionCustomRepository {

    
    
}
