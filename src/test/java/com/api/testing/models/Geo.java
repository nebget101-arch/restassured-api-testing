package com.api.testing.models;

/**
 * POJO class representing Geo coordinates
 */
public class Geo {
    private String lat;
    private String lng;

    // Constructors
    public Geo() {
    }

    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    // Getters and Setters
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
