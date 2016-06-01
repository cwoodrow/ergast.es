package com.cwoodrow.ergast.es.model;

import java.sql.Time;
import java.util.Date;

public class Race {
    public long year;
    public long round;
    public long circuitId;
    public String name;
    public Date date;
    public Time time;
    public String url;

    public Race(long year, long round, long circuitId, String name, Date date, Time time, String url) {
        this.year = year;
        this.round = round;
        this.circuitId = circuitId;
        this.name = name;
        this.date = date;
        this.time = time;
        this.url = url;
    }
}
