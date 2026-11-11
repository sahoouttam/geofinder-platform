package com.geofinder.platform.dto.request;

import java.util.List;

import com.geofinder.platform.entity.PlaceType;

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
public class CreateUserRequest {
    
    private String name;

    private String email;  
    
    @NotNull @DecimalMin("-90") @DecimalMax("90") 
    private Double homeLatitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180") 
    private Double homeLongitude;

    @NotNull @DecimalMin("-90") @DecimalMax("90") 
    private Double workLatitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180") 
    private Double workLongitude;

    private List<PlaceType> preferredTypes;
}
