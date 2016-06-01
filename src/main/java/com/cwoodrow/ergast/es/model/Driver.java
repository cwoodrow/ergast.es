package com.cwoodrow.ergast.es.model;

import java.util.Date;

public class Driver {
    public String driverRef;
    public long number;
    public String code;
    public String forename;
    public String surname;
    public Date dob;
    public String nationality;
    public String url;

    public Driver(String driverRef, long number, String code, String forename, String surname, Date dob, String nationality, String url) {
        this.driverRef = driverRef;
        this.number = number;
        this.code = code;
        this.forename = forename;
        this.surname = surname;
        this.dob = dob;
        this.nationality = nationality;
        this.url = url;
    }
}
