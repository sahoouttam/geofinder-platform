package com.geofinder.platform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.geofinder.platform.entity.Place;

@Repository
public interface PlaceRepository extends MongoRepository<Place, String>, PlaceCustomRepository {
    
}
