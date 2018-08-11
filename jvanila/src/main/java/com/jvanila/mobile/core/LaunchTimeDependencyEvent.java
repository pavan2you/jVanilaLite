package com.jvanila.mobile.core;

import com.jvanila.mobile.data.DataObject;

/**
 * Created by pavan on 05/08/18
 */
public class LaunchTimeDependencyEvent extends DataObject {

    public static final String CLASS_NAME = LaunchTimeDependencyEvent.class.getName();

    public boolean done;
    public Throwable error;
    public String className;

    public LaunchTimeDependencyEvent(String className, boolean done, Throwable error) {
        this.className = className;
        this.done = done;
        this.error = error;
    }
}
