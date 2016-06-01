package com.cwoodrow.ergast.es.model;

public class Constructor {
    public long constructorId;
    public String constructorRef;
    public String name;
    public String nationality;
    public String url;

    public Constructor(long constructorId, String constructorRef, String name, String nationality, String url) {
        this.constructorId = constructorId;
        this.constructorRef = constructorRef;
        this.name = name;
        this.nationality = nationality;
        this.url = url;
    }
}
