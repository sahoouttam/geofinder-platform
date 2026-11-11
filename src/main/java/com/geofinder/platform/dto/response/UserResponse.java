package com.geofinder.platform.dto.response;

import java.util.List;

import com.geofinder.platform.entity.PlaceType;
import com.geofinder.platform.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;

    private String name;

    private String email;                 

    private List<PlaceType> preferredTypes;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .preferredTypes(user.getPreferredTypes())
                .build();
    }
}
