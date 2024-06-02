package com.iri5.medicine_management.Model;

public class CurrentPoint {
    private double lat;
    private double lon;

    public CurrentPoint(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
