// IRunningCallback.aidl
package com.bz.app;
import com.bz.app.Person;

// Declare any non-default types here with import statements

interface IRunningCallback {

    void notify(long time);

    void pass(in Person p);
}
