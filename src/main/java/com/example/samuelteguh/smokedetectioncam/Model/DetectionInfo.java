package com.example.samuelteguh.smokedetectioncam.Model;

public class DetectionInfo {
    private String date;
    //private String value;
    private String url;
    private String time;
    private String coDValue;
    private String smokeDValue;

    public DetectionInfo(){

    }

    public DetectionInfo(String date, String url, String time, String coDValue, String smokeDValue) {
        this.date = date;
        this.url = url;
        this.time = time;
        this.coDValue = coDValue;
        this.smokeDValue = smokeDValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCoDValue() {
        return coDValue;
    }

    public void setCoDValue(String coDValue) {
        this.coDValue = coDValue;
    }

    public String getSmokeDValue() {
        return smokeDValue;
    }

    public void setSmokeDValue(String smokeDValue) {
        this.smokeDValue = smokeDValue;
    }
}
