// IRunning.aidl
package com.bz.app;
import com.bz.app.IRunningCallback;
// Declare any non-default types here with import statements

interface IRunning {

    void start();
    void stop();
    boolean isRunning();

    void registCallback(IRunningCallback callback);
    void unregistCallback(IRunningCallback callback);
}
