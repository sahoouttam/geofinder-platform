package com.geofinder.platform.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.geofinder.platform.entity.DeliveryZone;

@Repository
public interface DeliveryZoneRepository extends MongoRepository<DeliveryZone, String>, 
                                    DeliveryZoneCustomRepository {
    
}
