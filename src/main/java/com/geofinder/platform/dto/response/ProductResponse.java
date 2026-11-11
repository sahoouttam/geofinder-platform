package com.geofinder.platform.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.geofinder.platform.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    private String id;

    private String name;

    private String description;

    private String category;              

    private BigDecimal price;

    private boolean veg;

    private Boolean available;

    private List<String> tags;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .category(product.getCategory())
                        .price(product.getPrice())
                        .veg(product.isVeg())
                        .available(product.getAvailable())
                        .tags(product.getTags())
                        .build();
    }
}
