package com.cwoodrow.ergast.es.model;

public class Circuit {
    public String circuitRef;
    public String name;
    public String location;
    public String country;
    public double[] geoloc;
    public long alt;
    public String url;

    public Circuit(String circuitRef, String name, String location, String country, double lat, double lng, long alt, String url) {
        this.circuitRef = circuitRef;
        this.name = name;
        this.location = location;
        this.country = country;
        this.geoloc = new double[]{lng, lat};
        this.alt = alt;
        this.url = url;
    }
}
