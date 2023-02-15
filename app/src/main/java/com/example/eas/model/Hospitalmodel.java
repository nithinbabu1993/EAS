package com.example.eas.model;

public class Hospitalmodel {
    String devId,name,phone,address,utype,hlatitude,hlongitude;

    public Hospitalmodel(String devId, String name, String phone, String address, String utype, String hlatitude, String hlongitude) {
        this.devId = devId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.utype = utype;
        this.hlatitude = hlatitude;
        this.hlongitude = hlongitude;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getHlatitude() {
        return hlatitude;
    }

    public void setHlatitude(String hlatitude) {
        this.hlatitude = hlatitude;
    }

    public String getHlongitude() {
        return hlongitude;
    }

    public void setHlongitude(String hlongitude) {
        this.hlongitude = hlongitude;
    }
}
