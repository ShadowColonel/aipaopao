package com.bz.app.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ThinkPad User on 2016/11/15.
 */

public class RunningRecord implements Parcelable {

    private float distance;  //距离
    private String totalTime;  //跑步时间
    private String startTime;  //开始时间

    public RunningRecord(float distance, String totalTime, String startTime) {
        this.distance = distance;
        this.totalTime = totalTime;
        this.startTime = startTime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "RunningRecord{" +
                "distance=" + distance +
                ", totalTime='" + totalTime + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.distance);
        dest.writeString(this.totalTime);
        dest.writeString(this.startTime);
    }

    protected RunningRecord(Parcel in) {
        this.distance = in.readFloat();
        this.totalTime = in.readString();
        this.startTime = in.readString();
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
