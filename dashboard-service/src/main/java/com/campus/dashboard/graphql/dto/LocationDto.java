package com.campus.dashboard.graphql.dto;

import java.util.List;

public class LocationDto {

    private final double lat;
    private final double lng;
    private final String zone;
    private final List<String> affectedBuildings;

    public LocationDto(double lat, double lng, String zone, List<String> affectedBuildings) {
        this.lat = lat;
        this.lng = lng;
        this.zone = zone;
        this.affectedBuildings = affectedBuildings;
    }

    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getZone() { return zone; }
    public List<String> getAffectedBuildings() { return affectedBuildings; }
}
