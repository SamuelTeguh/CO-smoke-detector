package com.example.samuelteguh.smokedetectioncam.Model;

public class Token {
    private String fcmToken;

    public Token(){

    }

    public Token(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
