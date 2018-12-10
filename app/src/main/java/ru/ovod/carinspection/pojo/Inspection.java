package ru.ovod.carinspection.pojo;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Inspection {
    private int _inspectionid;
    private int number;
    private int orderid;
    private int issync;
    private Date date;
    private String model;
    private String vin;
    private int photoCo;

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
    private  String getIssyncString() {
        if (this.issync == 1) { return "Загружен"; } else { return ""; }
    }


    public void setIssync(int issync) {
        this.issync = issync;
    }

    public Date getDate() {
        return date;
    }
    private  String getDateString() {
        String dt;
        if (date.getTime() == 0) {
            dt = "";
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            dt = dateFormat.format(date);
        }
        return dt;
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


    @NonNull
    public String toString() {
        return String.valueOf(number) + " " + getDateString() + " " + model + " " + vin + " " + getIssyncString() + " ";
    }
}
