package com.campus.alertprocessing;

import com.campus.alertprocessing.service.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoServiceTest {

    private GeoService geoService;

    @BeforeEach
    void setUp() {
        geoService = new GeoService();
    }

    @Test
    void findAffectedBuildings_nearSAC_shouldReturnNearbyBuildings() {
        // SAC coordinates
        List<String> affected = geoService.findAffectedBuildings(40.9136, -73.1235);

        assertFalse(affected.isEmpty());
        assertTrue(affected.contains("Student Activities Center"));
    }

    @Test
    void findAffectedBuildings_withSmallRadius_shouldReturnFewerBuildings() {
        List<String> smallRadius = geoService.findAffectedBuildings(40.9136, -73.1235, 100);
        List<String> largeRadius = geoService.findAffectedBuildings(40.9136, -73.1235, 1000);

        assertTrue(smallRadius.size() <= largeRadius.size());
    }

    @Test
    void findAffectedBuildings_farFromCampus_shouldReturnEmpty() {
        // Coordinates far from campus
        List<String> affected = geoService.findAffectedBuildings(41.0, -74.0);

        assertTrue(affected.isEmpty());
    }

    @Test
    void haversineDistance_samePoint_shouldBeZero() {
        double distance = geoService.haversineDistance(40.9136, -73.1235, 40.9136, -73.1235);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void haversineDistance_knownPoints_shouldBeAccurate() {
        // Approximate distance between SAC and Engineering Building (~250m)
        double distance = geoService.haversineDistance(40.9136, -73.1235, 40.9142, -73.1260);
        assertTrue(distance > 100 && distance < 500,
                "Distance should be between 100m and 500m, got: " + distance);
    }
}
