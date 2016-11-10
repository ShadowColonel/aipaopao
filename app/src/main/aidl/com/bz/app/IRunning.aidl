// IRunning.aidl
package com.bz.app;
import com.bz.app.IRunningCallback;

// Declare any non-default types here with import statements

interface IRunning {

    void start(int type);

    void stop();


    void registCallback(IRunningCallback callback);

    void unregistCallback(IRunningCallback callback);
}
