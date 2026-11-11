package com.geofinder.platform.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateRequest {
    
    @NotNull @DecimalMin("-90") @DecimalMax("90") 
    private Double latitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180") 
    private Double longitude;
}