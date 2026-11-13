package com.geofinder.platform.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geofinder.platform.dto.request.CreateProductRequest;
import com.geofinder.platform.dto.response.NearbyPlace;
import com.geofinder.platform.dto.response.ProductResponse;
import com.geofinder.platform.dto.response.ProductSearchResponse;
import com.geofinder.platform.entity.Product;
import com.geofinder.platform.exception.PlaceNotFoundException;
import com.geofinder.platform.exception.ProductNotFoundException;
import com.geofinder.platform.repository.ProductRepository;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final PlaceService placeService;

    private static final int MAX_NEARBY_PLACES = 200;

    @Autowired
    public ProductService(ProductRepository productRepository, PlaceService placeService) {
        this.productRepository = productRepository;
        this.placeService = placeService;
    }

    public ProductResponse createProduct(CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                            .placeId(createProductRequest.getPlaceId())
                            .name(createProductRequest.getName())
                            .description(createProductRequest.getDescription())
                            .category(createProductRequest.getCategory())
                            .price(createProductRequest.getPrice())
                            .veg(createProductRequest.isVeg())
                            .available(createProductRequest.getAvailable())
                            .tags(createProductRequest.getTags())
                            .build();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    public List<ProductResponse> searchProducts(String text, int limit) {
        return productRepository.findByText(text, limit)
                        .stream()
                        .map(ProductResponse::from)
                        .collect(Collectors.toList());
    }

    public List<ProductSearchResponse> searchNearbyProducts(String text, double latitude, 
                                        double longitude, double radiusKm, int limit) {
        List<NearbyPlace> places = placeService.findNearby(
                                    latitude, longitude, radiusKm, MAX_NEARBY_PLACES);
        Map<String, NearbyPlace> nearByPlaces = places.stream()
                    .collect(Collectors.toMap(NearbyPlace::getId, Function.identity()));
        List<String> placeIds = new ArrayList<>(nearByPlaces.keySet()); 
        return productRepository.findAllByTextAndPlaceIds(text, placeIds)
                        .stream()
                        .map(product -> ProductSearchResponse.of(
                            product, nearByPlaces.get(product.getPlaceId())))
                        .sorted(Comparator.comparingDouble(ProductSearchResponse::getDistanceKm))
                        .collect(Collectors.toList());
    }

    public List<ProductResponse> findAllByPlace(String placeId, String category, 
                                        Boolean veg, Boolean available, int limit) {
        if (!placeService.exitsByPlaceId(placeId)) {
            throw new PlaceNotFoundException(placeId);
        }
        return productRepository.findAllByPlace(placeId, category, veg, available, limit)
                        .stream()
                        .map(ProductResponse::from)
                        .collect(Collectors.toList());
    }

    public ProductResponse updateAvailability(String id, Boolean available) {
        return productRepository.updateProductAvailability(id, available)
                        .map(ProductResponse::from)
                        .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
