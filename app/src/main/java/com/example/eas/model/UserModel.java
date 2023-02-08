package com.example.eas.model;

public class UserModel {
    String devId,name,phone,address,utype;

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public UserModel(String devId, String name, String phone, String address,String utype) {
        this.devId = devId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.utype = utype;
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
}
