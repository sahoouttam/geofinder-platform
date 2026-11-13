package com.geofinder.platform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geofinder.platform.dto.request.CreateProductRequest;
import com.geofinder.platform.dto.response.ProductResponse;
import com.geofinder.platform.dto.response.ProductSearchResponse;
import com.geofinder.platform.service.ProductService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
                    @Valid @RequestBody CreateProductRequest createProductRequest) {
        ProductResponse productResponse = productService.createProduct(createProductRequest);
        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> search(@RequestParam String text,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        List<ProductResponse> productResponses = productService.searchProducts(text, limit);
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @GetMapping("/search-nearby")
    public ResponseEntity<List<ProductSearchResponse>> searchNearbyProducts(
                        @RequestParam String text,
                        @RequestParam double latitude, 
                        @RequestParam double longitude, 
                        @RequestParam double radiusKm,
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        List<ProductSearchResponse> productSearchResponses = productService
                    .searchNearbyProducts(text, latitude, longitude, radiusKm, limit);
        return new ResponseEntity<>(productSearchResponses, HttpStatus.OK);
    }

    @GetMapping("/places/{placeId}")
    public ResponseEntity<List<ProductResponse>> searchAllByPlace(
                        @PathVariable String placeId, 
                        @RequestParam(required = false) String category, 
                        @RequestParam(required = false) Boolean veg, 
                        @RequestParam(required = false) Boolean available, 
                        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
        List<ProductResponse> productResponses = productService
                    .findAllByPlace(placeId, category, veg, available, limit);
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }
    
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ProductResponse> updateAvailability(@PathVariable String id,
                                            @RequestParam Boolean available) {
        ProductResponse productResponse = productService.updateAvailability(id, available);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
}
