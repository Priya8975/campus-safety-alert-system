package com.campus.alertprocessing.model;

public class CampusBuilding {

    private final String name;
    private final double lat;
    private final double lng;

    public CampusBuilding(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
