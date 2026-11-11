package com.geofinder.platform.entity;

public enum PlaceType {
    
    RESTAURANT, GROCERY, PHARMACY, GYM, HOTEL, CAFE, MALL;

    public boolean supportsDelivery() {
        return this == RESTAURANT || this == GROCERY || this == PHARMACY;
    }
}
