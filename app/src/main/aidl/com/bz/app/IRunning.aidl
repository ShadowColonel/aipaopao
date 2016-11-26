// IRunning.aidl
package com.bz.app;
import com.bz.app.IRunningCallback;
// Declare any non-default types here with import statements

interface IRunning {

    void start();
    void stop();
    void pause();
    void resume();
    boolean isRunning();
    void openNotification();
    void closeNotification();
    void chooseLocationMode(int locationMode);

    void registCallback(IRunningCallback callback);
    void unregistCallback(IRunningCallback callback);
}
