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

import com.jvanila.IPlatform;
import com.jvanila.PlatformLocator;
import com.jvanila.core.eventbus.IEventSubscriber;
import com.jvanila.core.objectflavor.ComparableObject;

/**
 * Created by pavan on 04/08/18
 */
public abstract class LaunchTimeDependency extends ComparableObject {

    private static final int STATE_IDLE     = 0;
    private static final int STATE_LOADING  = 1;
    private static final int STATE_LOADED   = 2;

    private IEventSubscriber mObserver;
    private int mCurrentState;

    public LaunchTimeDependency(IEventSubscriber subscriber) {
        setState(STATE_IDLE);
        mObserver = subscriber;
        subscribe(mObserver);
    }

    private void setState(int state) {
        mCurrentState = state;
    }

    protected void subscribe(IEventSubscriber observer) {
        IPlatform platform = PlatformLocator.getPlatform();
        platform.getEventBus().subscribe(loadEventName(), observer);
    }

    public void unsubscribe() {
        IPlatform platform = PlatformLocator.getPlatform();
        platform.getEventBus().subscribe(loadEventName(), mObserver);
    }

    public boolean isLoaded() {
        return mCurrentState == STATE_LOADED;
    }

    public boolean needLoading() {
        return mCurrentState == STATE_IDLE;
    }

    protected abstract String loadEventName();

    public abstract void load();

    public abstract void cancel();

}
