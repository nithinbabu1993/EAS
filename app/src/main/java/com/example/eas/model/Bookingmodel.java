package com.example.eas.model;

public class Bookingmodel {
     public static String latitude=null,longitude=null;
    String uid,ambulanceId,hospitalId,uname,uaddress,uphone,ulatitude,ulongitude,ambNo,driverName,driverPhone,bdate;

    public Bookingmodel(String uid, String ambulanceId, String hospitalId, String uname, String uaddress, String uphone, String ulatitude, String ulongitude, String ambNo, String driverName, String driverPhone, String bdate) {
        this.uid = uid;
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
        this.uname = uname;
        this.uaddress = uaddress;
        this.uphone = uphone;
        this.ulatitude = ulatitude;
        this.ulongitude = ulongitude;
        this.ambNo = ambNo;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.bdate = bdate;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAmbulanceId() {
        return ambulanceId;
    }

    public void setAmbulanceId(String ambulanceId) {
        this.ambulanceId = ambulanceId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUaddress() {
        return uaddress;
    }

    public void setUaddress(String uaddress) {
        this.uaddress = uaddress;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public String getUlatitude() {
        return ulatitude;
    }

    public void setUlatitude(String ulatitude) {
        this.ulatitude = ulatitude;
    }

    public String getUlongitude() {
        return ulongitude;
    }

    public void setUlongitude(String ulongitude) {
        this.ulongitude = ulongitude;
    }

    public String getAmbNo() {
        return ambNo;
    }

    public void setAmbNo(String ambNo) {
        this.ambNo = ambNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Bookingmodel(String uid, String ambulanceId, String hospitalId, String uname, String uaddress, String uphone, String ulatitude, String ulongitude, String ambNo, String driverName, String driverPhone) {
        this.uid = uid;
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
        this.uname = uname;
        this.uaddress = uaddress;
        this.uphone = uphone;
        this.ulatitude = ulatitude;
        this.ulongitude = ulongitude;
        this.ambNo = ambNo;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
    }
}
