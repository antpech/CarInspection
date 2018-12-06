package ru.ovod.carinspection.pojo;

public class Photo {
    int _photoid;
    String path;
    String name;
    int issync;
    int inspectionid;

    public Photo(int _photoid, String path, String name, int issync, int inspectionid) {
        this._photoid = _photoid;
        this.path = path;
        this.name = name;
        this.issync = issync;
        this.inspectionid = inspectionid;
    }

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
