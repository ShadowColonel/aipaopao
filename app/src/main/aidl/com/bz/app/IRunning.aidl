// IRunning.aidl
package com.bz.app;
import com.bz.app.IRunningCallback;
// Declare any non-default types here with import statements

interface IRunning {

    void start();
    void stop();
    void location();

    void registCallback(IRunningCallback callback);
    void unregistCallback(IRunningCallback callback);
}
