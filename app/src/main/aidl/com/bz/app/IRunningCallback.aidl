// IRunningCallback.aidl
package com.bz.app;
// Declare any non-default types here with import statements

interface IRunningCallback {

    void notifyTime(long time);
    void notifyData(float distance, String latLngsListStr, String nowLatLngStr);
}
