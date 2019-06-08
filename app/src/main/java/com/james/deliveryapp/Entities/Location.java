package com.james.deliveryapp.Entities;

// The location of the delivery item
public class Location {
    private double lat;
    private double lng;
    private String address;

    public Location(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }
}
