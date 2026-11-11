package com.geofinder.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geofinder.platform.dto.request.CreateUserRequest;
import com.geofinder.platform.dto.request.LocationUpdateRequest;
import com.geofinder.platform.dto.response.UserResponse;
import com.geofinder.platform.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody 
                                            CreateUserRequest createUserRequest) {
        UserResponse userResponse = userService.createUser(createUserRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUser(@PathVariable String id) {
        UserResponse userResponse = userService.findUser(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping("/{id}/locations")
    public ResponseEntity<UserResponse> updateLocations(@PathVariable String id,
                    @Valid @RequestBody LocationUpdateRequest locationUpdateRequest) {
        UserResponse userResponse = userService.updateLocations(id, locationUpdateRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
