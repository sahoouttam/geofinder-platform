package com.geofinder.platform.repository;

import java.util.List;
import java.util.Optional;

import com.geofinder.platform.entity.Product;

public interface ProductCustomRepository {
    
    List<Product> findByText(String text, int limit);

    List<Product> findAllByTextAndPlaceIds(String text, List<String> placeIds);

    List<Product> findAllByPlace(String placeId, String category, 
                                Boolean veg, Boolean available, int limit);

    Optional<Product> updateProductAvailability(String id, Boolean available);
}
