package com.example.testingcustomerapp;

public class User {

    double lat;
    double lon;

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public User(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
