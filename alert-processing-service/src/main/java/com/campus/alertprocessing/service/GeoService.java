package com.campus.alertprocessing.service;

import com.campus.alertprocessing.model.CampusBuilding;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeoService {

    private static final double DEFAULT_RADIUS_METERS = 500.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private static final List<CampusBuilding> CAMPUS_BUILDINGS = List.of(
            new CampusBuilding("Student Activities Center", 40.9136, -73.1235),
            new CampusBuilding("Staller Center", 40.9158, -73.1215),
            new CampusBuilding("Frank Melville Jr. Memorial Library", 40.9148, -73.1234),
            new CampusBuilding("Engineering Building", 40.9142, -73.1260),
            new CampusBuilding("Computer Science Building", 40.9118, -73.1230),
            new CampusBuilding("Chemistry Building", 40.9132, -73.1249),
            new CampusBuilding("Physics Building", 40.9126, -73.1253),
            new CampusBuilding("Earth and Space Sciences", 40.9120, -73.1247),
            new CampusBuilding("Life Sciences Building", 40.9112, -73.1238),
            new CampusBuilding("Social and Behavioral Sciences", 40.9140, -73.1220),
            new CampusBuilding("Javits Lecture Center", 40.9134, -73.1242),
            new CampusBuilding("Harriman Hall", 40.9155, -73.1240),
            new CampusBuilding("Administration Building", 40.9160, -73.1230),
            new CampusBuilding("Student Union", 40.9150, -73.1250),
            new CampusBuilding("Recreation Center", 40.9105, -73.1220),
            new CampusBuilding("Stadium", 40.9090, -73.1200),
            new CampusBuilding("Health Sciences Center", 40.9095, -73.1175),
            new CampusBuilding("Hospital", 40.9085, -73.1165),
            new CampusBuilding("Roth Quad Residence", 40.9170, -73.1260),
            new CampusBuilding("Tabler Quad Residence", 40.9175, -73.1280)
    );

    public List<String> findAffectedBuildings(double lat, double lng) {
        return findAffectedBuildings(lat, lng, DEFAULT_RADIUS_METERS);
    }

    public List<String> findAffectedBuildings(double lat, double lng, double radiusMeters) {
        List<String> affected = new ArrayList<>();
        for (CampusBuilding building : CAMPUS_BUILDINGS) {
            double distance = haversineDistance(lat, lng, building.getLat(), building.getLng());
            if (distance <= radiusMeters) {
                affected.add(building.getName());
            }
        }
        return affected;
    }

    /**
     * Calculates the distance between two geographic coordinates using the Haversine formula.
     * Returns distance in meters.
     */
    public double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}
