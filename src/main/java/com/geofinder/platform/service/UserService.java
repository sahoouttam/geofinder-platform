package com.geofinder.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import com.geofinder.platform.dto.request.CreateUserRequest;
import com.geofinder.platform.dto.request.LocationUpdateRequest;
import com.geofinder.platform.dto.response.UserResponse;
import com.geofinder.platform.entity.User;
import com.geofinder.platform.exception.UserNotFoundException;
import com.geofinder.platform.exception.UserUpdateException;
import com.geofinder.platform.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = User.builder()
            .name(createUserRequest.getName())
            .email(createUserRequest.getEmail())
            .homeLocation(new GeoJsonPoint(createUserRequest.getHomeLongitude(), 
                        createUserRequest.getHomeLatitude()))
            .workLocation(new GeoJsonPoint(createUserRequest.getWorkLongitude(), 
                        createUserRequest.getWorkLatitude()))
            .preferredTypes(createUserRequest.getPreferredTypes())
            .build();
        User savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    public UserResponse findUser(String id) {
        User user = findById(id);
        return UserResponse.from(user);
    }

    public UserResponse updateLocations(String id, LocationUpdateRequest locationUpdateRequest) {
        boolean validLocationUpdate = userRepository
                            .updateLocations(id, locationUpdateRequest);
        if (!validLocationUpdate) {
            throw new UserUpdateException("failed to update user " + id);
        }
        return findUser(id);
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
