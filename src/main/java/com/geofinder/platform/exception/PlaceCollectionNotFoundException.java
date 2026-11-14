package com.geofinder.platform.exception;

public class PlaceCollectionNotFoundException extends RuntimeException {
    public PlaceCollectionNotFoundException(String id) {
        super("Place collection not found with id: " + id);
    }
}
