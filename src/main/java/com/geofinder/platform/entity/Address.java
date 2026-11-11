package com.geofinder.platform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Address {
    
    private String line;
    private String area;
    private String city;
    private String state;
    private String postalCode;
}
