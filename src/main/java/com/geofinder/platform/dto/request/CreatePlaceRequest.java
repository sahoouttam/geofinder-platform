package com.geofinder.platform.dto.request;

import java.util.List;

import com.geofinder.platform.entity.Address;
import com.geofinder.platform.entity.Attribute;
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
public class CreatePlaceRequest {
    
    private String name;
    private PlaceType placeType;
    private List<String> tags;
    private String description;

    @NotNull @DecimalMin("-90") @DecimalMax("90") 
    private Double latitude;

    @NotNull @DecimalMin("-180") @DecimalMax("180") 
    private Double longitude;

    private Address address;
    private List<OperatingHoursRequest> operatingHours;
    private List<Attribute> attributes;
    private String parentPlaceId;
}
