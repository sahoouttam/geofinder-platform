package com.geofinder.platform.dto.response;

import java.time.LocalDateTime;

import com.geofinder.platform.entity.PlaceCollection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCollectionResponse {
    
    private String id;

    private String userId;                 

    private String name;                   

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static PlaceCollectionResponse from(PlaceCollection placeCollection) {
        return new PlaceCollectionResponse(placeCollection.getId(), 
                            placeCollection.getUserId(), 
                            placeCollection.getName(), 
                            placeCollection.getCreatedAt(), 
                            placeCollection.getUpdatedAt()
                );
    }

}
