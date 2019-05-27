package com.example.samuelteguh.smokedetectioncam.Model;

public class Sensor {
    private String sensorID, coValue, smokeValue;

    public Sensor(){

    }

    public Sensor(String sensorID, String coValue, String smokeValue) {
        this.sensorID = sensorID;
        this.coValue = coValue;
        this.smokeValue = smokeValue;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getCoValue() {
        return coValue;
    }

    public void setCoValue(String coValue) {
        this.coValue = coValue;
    }

    public String getSmokeValue() {
        return smokeValue;
    }

    public void setSmokeValue(String smokeValue) {
        this.smokeValue = smokeValue;
    }
}
