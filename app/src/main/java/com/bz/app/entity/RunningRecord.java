package com.bz.app.entity;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad User on 2016/11/15.
 * 一条跑步轨迹，包括了起点，终点，轨迹中间点，距离，跑步用，速度，开始时间
 */

public class RunningRecord {

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

    public void addPoint(LatLng point) {
        pathLinePoints.add(point);
    }

    @Override
    public String toString() {
        StringBuilder record = new StringBuilder();
        record.append("recordsize:" + getPathLinePoints().size() + ",");
        record.append("distance:" + getDistance() + "m,");
        record.append("duration:" + getDuration());
        return record.toString();
    }
}
