package com.bz.app.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad User on 2016/11/15.
 * 一条跑步轨迹，包括了起点，终点，轨迹中间点，距离，跑步用，速度，开始时间
 */

public class RunningRecord implements Parcelable {

    private LatLng startPoint;
    private LatLng endPoint;
    private List<LatLng> pathLinePoints = new ArrayList<>();
    private String distance;
    private String duration;
    private String averageSpeed;
    private String date;
    private int id = 0;

    public RunningRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public List<LatLng> getPathLinePoints() {
        return pathLinePoints;
    }

    public void setPathLinePoints(ArrayList<LatLng> pathLinePoints) {
        this.pathLinePoints = pathLinePoints;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.startPoint, flags);
        dest.writeParcelable(this.endPoint, flags);
        dest.writeTypedList(this.pathLinePoints);
        dest.writeString(this.distance);
        dest.writeString(this.duration);
        dest.writeString(this.averageSpeed);
        dest.writeString(this.date);
        dest.writeInt(this.id);
    }

    protected RunningRecord(Parcel in) {
        this.startPoint = in.readParcelable(LatLng.class.getClassLoader());
        this.endPoint = in.readParcelable(LatLng.class.getClassLoader());
        this.pathLinePoints = in.createTypedArrayList(LatLng.CREATOR);
        this.distance = in.readString();
        this.duration = in.readString();
        this.averageSpeed = in.readString();
        this.date = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<RunningRecord> CREATOR = new Parcelable.Creator<RunningRecord>() {
        @Override
        public RunningRecord createFromParcel(Parcel source) {
            return new RunningRecord(source);
        }

        @Override
        public RunningRecord[] newArray(int size) {
            return new RunningRecord[size];
        }
    };
}
