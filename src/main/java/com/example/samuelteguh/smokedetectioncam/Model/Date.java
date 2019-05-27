package com.example.samuelteguh.smokedetectioncam.Model;

public class Date {
    private String dateSort;
    private String count;

    public Date(){

    }

    public Date(String dateSort, String count) {
        this.dateSort = dateSort;
        this.count = count;
    }

    public String getDateSort() {
        return dateSort;
    }

    public void setDateSort(String dateSort) {
        this.dateSort = dateSort;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
