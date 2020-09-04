package com.example.setripverifier.model;

public class TripModel {
    String inTime, outTime, lokasi, status, uid, verifikasi;

    public TripModel(){

    }

    public TripModel(String inTime, String outTime, String lokasi, String status, String uid, String verifikasi) {
        this.inTime = inTime;
        this.outTime = outTime;
        this.lokasi = lokasi;
        this.status = status;
        this.uid = uid;
        this.verifikasi = verifikasi;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVerifikasi() {
        return verifikasi;
    }

    public void setVerifikasi(String verifikasi) {
        this.verifikasi = verifikasi;
    }
}
