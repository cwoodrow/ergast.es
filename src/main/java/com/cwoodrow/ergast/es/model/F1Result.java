package com.cwoodrow.ergast.es.model;

public class F1Result {
    public long number;
    public long grid;
    public long position;
    public String positionText;
    public long positionOrder;
    public double points;
    public long laps;
    public String time;
    public long milliseconds;
    public long fastestLap;
    public long rank;
    public String fastestLapTime;
    public String fastestLapSpeed;
    public String status;

    public Driver driver;
    public Race race;
    public Circuit circuit;
    public Constructor constructor;
    public Season season;

    public F1Result(long number, long grid, long position, String positionText, long positionOrder, double points, long laps, String time, long milliseconds, long fastestLap, long rank, String fastestLapTime, String fastestLapSpeed, String status, Driver driver, Race race, Circuit circuit, Constructor constructor, Season season) {
        this.number = number;
        this.grid = grid;
        this.position = position;
        this.positionText = positionText;
        this.positionOrder = positionOrder;
        this.points = points;
        this.laps = laps;
        this.time = time;
        this.milliseconds = milliseconds;
        this.fastestLap = fastestLap;
        this.rank = rank;
        this.fastestLapTime = fastestLapTime;
        this.fastestLapSpeed = fastestLapSpeed;
        this.status = status;
        this.driver = driver;
        this.race = race;
        this.circuit = circuit;
        this.constructor = constructor;
        this.season = season;
    }
}
