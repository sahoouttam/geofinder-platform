package com.geofinder.platform.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.geofinder.platform.entity.PlaceCollection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCollectionDetailResponse {

    private String id;
    private String userId;
    private String name;
    private int placeCount;
    private List<PlaceResponse> places;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlaceCollectionDetailResponse from(PlaceCollection collection,
                                                     List<PlaceResponse> places) {
        return PlaceCollectionDetailResponse.builder()
                .id(collection.getId())
                .userId(collection.getUserId())
                .name(collection.getName())
                .placeCount(places.size())
                .places(places)
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }
}