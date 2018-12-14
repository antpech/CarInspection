package ru.ovod.carinspection.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Order implements Parcelable {
    private int orderid;
    private int number;
    private Date date;
    private String model;
    private String vin;
    private int inspectionID;

    public Order(int orderid, int number, Date date, String model, String vin) {
        this.orderid = orderid;
        this.number = number;
        this.date = date;
        this.model = model;
        this.vin = vin;
    }

    public Order(Parcel in) {
        orderid = in.readInt();
        number = in.readInt();
        date = new Date(in.readLong());
        model = in.readString();
        vin = in.readString();
        inspectionID = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderid);
        dest.writeInt(number);
        dest.writeLong(date.getTime());
        dest.writeString(model);
        dest.writeString(vin);
        dest.writeInt(inspectionID);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        // распаковываем объект из Parcel
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public int getInspectionID() {
        return inspectionID;
    }

    public void setInspectionID(int inspectionID) {
        this.inspectionID = inspectionID;
    }
}
