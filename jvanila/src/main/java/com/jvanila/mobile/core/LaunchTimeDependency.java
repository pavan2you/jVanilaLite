/*
 * Copyright (C) 2015 - 2018 The jVanila Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * author - pavan.jvanila@gmail.com
 */

package com.jvanila.mobile.core;

import com.jvanila.core.eventbus.IEventSubscriber;
import com.jvanila.core.objectflavor.ComparableObject;
import com.jvanila.core.objectflavor.IComparableObject;

/**
 * Created by pavan on 04/08/18
 */
public abstract class LaunchTimeDependency extends ComparableObject implements IEventSubscriber {

    protected static final int STATE_IDLE     = 0;
    protected static final int STATE_LOADING  = 1;
    protected static final int STATE_LOADED   = 2;
    protected static final int STATE_FAILED   = 3;

    protected int mCurrentState;
    protected Throwable mError;

    public LaunchTimeDependency() {
        setState(STATE_IDLE);
    }

    protected void setState(int state) {
        mCurrentState = state;
    }

    public boolean isLoaded() {
        return mCurrentState == STATE_LOADED;
    }

    public boolean needLoading() {
        return mCurrentState == STATE_IDLE;
    }

    public boolean isFailed() {
        return mCurrentState == STATE_FAILED;
    }

    public Throwable getError() {
        return mError;
    }

    public abstract void load();

    public abstract void cancel();

    @Override
    public boolean isEqualsTo(IComparableObject o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LaunchTimeDependency that = (LaunchTimeDependency) o;

        if (mCurrentState != that.mCurrentState) {
            return false;
        }
        if (mError != that.mError) {
            return false;
        }

        return true;
    }

    @Override
    public int hashOfObject() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mCurrentState;
        result = prime * result + ((mError == null) ? 0 : mError.hashCode());
        return result;
    }
}
