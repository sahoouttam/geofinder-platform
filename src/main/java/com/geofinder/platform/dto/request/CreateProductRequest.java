package com.geofinder.platform.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank
    private String placeId;               

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String category;              

    @NotNull 
    @Positive
    private BigDecimal price;

    private boolean veg;

    private Boolean available;

    private List<String> tags;

}
