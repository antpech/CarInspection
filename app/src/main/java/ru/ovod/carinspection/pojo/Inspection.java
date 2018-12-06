package ru.ovod.carinspection.pojo;

import java.util.Date;

public class Inspection {
    int _inspectionid;
    int number;
    int orderid;
    int issync;
    Date date;
    String model;
    String vin;
    int photoCo;

    public Inspection(int _inspectionid, int number, int orderid, int issync, Date date, String model, String vin) {
        this._inspectionid = _inspectionid;
        this.number = number;
        this.orderid = orderid;
        this.issync = issync;
        this.date = date;
        this.model = model;
        this.vin = vin;
    }

    public int get_inspectionid() {
        return _inspectionid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getIssync() {
        return issync;
    }

    public void setIssync(int issync) {
        this.issync = issync;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getPhotoCo() {
        return photoCo;
    }

    public void setPhotoCo(int photoCo) {
        this.photoCo = photoCo;
    }
}
