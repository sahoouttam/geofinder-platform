package com.geofinder.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GeoFinderPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeoFinderPlatformApplication.class, args);
    }
}