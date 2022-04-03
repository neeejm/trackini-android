package me.neeejm.trackini.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Position implements Serializable {
    private int id;
    private double longitude;
    private double latitude;
    private Date date;

    public Position(double longitude, double latitude, Date date) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setLongitude(double l) {
        longitude = l;
    }

    public void setLatitude(double l) {
        latitude = l;
    }

    public void setDate(Date d) {
        date = d;
    }
}
