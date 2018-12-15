package ru.ovod.carinspection.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Inspection implements Parcelable {
    //@SerializedName("orderid")
    private int orderid;

    //@SerializedName("number")
    private int number;

    private int _inspectionid;
    private int issync;
    private Date date;
    private String model;
    private String vin;
    private int photoCo;
    private String path;

    public Inspection(int _inspectionid, int number, int orderid, int issync, Date date, String model, String vin) {
        this._inspectionid = _inspectionid;
        this.number = number;
        this.orderid = orderid;
        this.issync = issync;
        this.date = date;
        this.model = model;
        this.vin = vin;
    }

    public Inspection(Parcel in) {
        orderid = in.readInt();
        number = in.readInt();
        _inspectionid = in.readInt();
        date = new Date(in.readLong());
        model = in.readString();
        vin = in.readString();
        photoCo = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderid);
        dest.writeInt(number);
        dest.writeInt(_inspectionid);
    }

    public static final Parcelable.Creator<Inspection> CREATOR = new Parcelable.Creator<Inspection>() {
        // распаковываем объект из Parcel
        public Inspection createFromParcel(Parcel in) {
            return new Inspection(in);
        }

        public Inspection[] newArray(int size) {
            return new Inspection[size];
        }
    };


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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @NonNull
    public String toString() {
        return String.valueOf(number) + " " + getDateString() + " " + model + " " + vin + " " + getIssyncString() + " ";
    }
}
