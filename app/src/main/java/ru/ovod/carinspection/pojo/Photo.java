package ru.ovod.carinspection.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private int _photoid;
    private String path;
    private String name;
    private int issync;
    private int inspectionid;

    public Photo(int _photoid, String path, String name, int issync, int inspectionid) {
        this._photoid = _photoid;
        this.path = path;
        this.name = name;
        this.issync = issync;
        this.inspectionid = inspectionid;
    }

    public Photo(Parcel in) {
        _photoid = in.readInt();
        path = in.readString();
        name = in.readString();
        issync = in.readInt();
        inspectionid = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_photoid);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeInt(inspectionid);
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        // распаковываем объект из Parcel
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public int get_photoid() {
        return _photoid;
    }

    public int getInspectionid() {
        return inspectionid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIssync() {
        return issync;
    }

    public void setIssync(int issync) {
        this.issync = issync;
    }
}
