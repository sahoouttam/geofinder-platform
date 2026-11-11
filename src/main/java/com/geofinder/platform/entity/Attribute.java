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
public class Attribute {
    
    private String key;
    private String value;
}
