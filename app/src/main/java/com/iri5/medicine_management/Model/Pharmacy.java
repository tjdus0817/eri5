package com.iri5.medicine_management.Model;

public class Pharmacy {
    private String uid;
    private String shop_name;
    private String operating_time;
    private String shop_desc;
    private String inventory;
    private double lat;
    private double lon;

    public Pharmacy(String uid, String shop_name, String operating_time, String shop_desc, String inventory, String lat, String lon){
        this.uid = uid;
        this.shop_name = shop_name;
        this.operating_time =operating_time;
        this.shop_desc = shop_desc;
        this.inventory = inventory;
        this.lat = Double.valueOf(lat);
        this.lon = Double.valueOf(lon);
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setOperating_time(String operating_time) {
        this.operating_time = operating_time;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setShop_desc(String shop_desc) {
        this.shop_desc = shop_desc;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getShop_desc() {
        return shop_desc;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getInventory() {
        return inventory;
    }

    public String getUid() {
        return uid;
    }

    public String getOperating_time() {
        return operating_time;
    }
}
