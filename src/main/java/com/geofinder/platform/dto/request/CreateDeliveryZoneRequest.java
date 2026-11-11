package com.geofinder.platform.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryZoneRequest {

    private String placeId;
    
    @NotEmpty 
    private List<List<Double>> rings;

    private Integer minOrderValue;
    
    private Integer deliveryFee;
}
