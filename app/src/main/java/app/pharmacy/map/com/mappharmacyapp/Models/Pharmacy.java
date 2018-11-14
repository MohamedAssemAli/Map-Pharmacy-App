package app.pharmacy.map.com.mappharmacyapp.Models;

import java.io.Serializable;

public class Pharmacy implements Serializable {

    private String uid, username, email, password, lat, lon;

    public Pharmacy() {
    }

    public Pharmacy(String uid, String username, String email, String password, String lat, String lon) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
