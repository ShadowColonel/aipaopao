// IRunningCallback.aidl
package com.bz.app;
// Declare any non-default types here with import statements

interface IRunningCallback {

    void notifyData(float distance, String latLngListStr, String nowLatLngStr);
    void timeUpdate(long time);
    void notSave();
}
