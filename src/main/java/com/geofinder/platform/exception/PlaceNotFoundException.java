package com.geofinder.platform.exception;

public class PlaceNotFoundException extends RuntimeException {
    public PlaceNotFoundException(String id) {
        super("Place not found with id: " + id);
    }
}
