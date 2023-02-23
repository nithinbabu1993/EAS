package com.example.eas.model;

public class AmbulanceModel {
    String aname,name,phone,hospId,utype,devId;

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
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

    public String getHospId() {
        return hospId;
    }

    public void setHospId(String hospId) {
        this.hospId = hospId;
    }

    public String getUtype() {
        return utype;
    }

    public void setUtype(String utype) {
        this.utype = utype;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public AmbulanceModel(String aname, String name, String phone, String hospId, String utype, String devId) {
        this.aname = aname;
        this.name = name;
        this.phone = phone;
        this.hospId = hospId;
        this.utype = utype;
        this.devId = devId;
    }
}
