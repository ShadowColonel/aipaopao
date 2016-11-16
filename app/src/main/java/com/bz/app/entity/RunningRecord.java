package com.bz.app.entity;

import com.amap.api.location.AMapLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad User on 2016/11/15.
 * 一条跑步轨迹，包括了起点，终点，轨迹中间点，距离，跑步用，速度，开始时间
 */

public class RunningRecord {

    private AMapLocation mStartPoint;
    private AMapLocation mEndPoint;
    private List<AMapLocation> mPathLinePoints = new ArrayList<>();
    private String mDistance;
    private String mDuration;
    private String mAveragespeed;
    private String mDate;
    private int mId = 0;

    public RunningRecord() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public AMapLocation getmStartPoint() {
        return mStartPoint;
    }

    public void setmStartPoint(AMapLocation mStartPoint) {
        this.mStartPoint = mStartPoint;
    }

    public AMapLocation getmEndPoint() {
        return mEndPoint;
    }

    public void setmEndPoint(AMapLocation mEndPoint) {
        this.mEndPoint = mEndPoint;
    }

    public List<AMapLocation> getmPathLinePoints() {
        return mPathLinePoints;
    }

    public void setmPathLinePoints(List<AMapLocation> mPathLinePoints) {
        this.mPathLinePoints = mPathLinePoints;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getmAveragespeed() {
        return mAveragespeed;
    }

    public void setmAveragespeed(String mAveragespeed) {
        this.mAveragespeed = mAveragespeed;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void addPoint(AMapLocation point) {
        mPathLinePoints.add(point);
    }

    @Override
    public String toString() {
        StringBuilder record = new StringBuilder();
        record.append("recordsize:" + getmPathLinePoints().size() + ",");
        record.append("distance:" + getmDistance() + "m,");
        record.append("duration:" + getmDuration() + "s");
        return record.toString();
    }
}
