package app.pharmacy.map.com.mappharmacyapp.Models;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class Order implements Serializable {

    private String uid;
    private String order;
    private int state;
    @PropertyName("pharmacy_uid")
    private String pharmacyUid;
    @PropertyName("pharmacy_name")
    private String pharmacyName;
    @PropertyName("user_uid")
    private String userUid;
    @PropertyName("user_name")
    private String username;

    public Order() {
    }

    public Order(String uid, String order, int state, String pharmacyUid, String pharmacyName, String userUid, String username) {
        this.uid = uid;
        this.order = order;
        this.state = state;
        this.pharmacyUid = pharmacyUid;
        this.pharmacyName = pharmacyName;
        this.userUid = userUid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @PropertyName("pharmacy_uid")
    public String getPharmacyUid() {
        return pharmacyUid;
    }

    public void setPharmacyUid(String pharmacyUid) {
        this.pharmacyUid = pharmacyUid;
    }

    @PropertyName("pharmacy_name")
    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    @PropertyName("user_uid")
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    @PropertyName("user_name")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
