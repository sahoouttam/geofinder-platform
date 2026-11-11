package com.geofinder.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geofinder.platform.dto.request.CreateProductRequest;
import com.geofinder.platform.dto.response.ProductResponse;
import com.geofinder.platform.entity.Product;
import com.geofinder.platform.repository.ProductRepository;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}
