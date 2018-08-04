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

package com.jvanila.droid;

import com.jvanila.IPlatform;
import com.jvanila.core.IThread;
import com.jvanila.core.di.IInjector;
import com.jvanila.core.eventbus.IEventBus;
import com.jvanila.core.log.ILogger;
import com.jvanila.droid.eventbus.EventBus;
import com.jvanila.droid.log.Logger;
import com.jvanila.droid.wrapper.ThreadWrapper;
import com.jvanila.mobile.core.IApplication;

public class Platform implements IPlatform {

    private boolean mIsReady;

    private IApplication mApplication;
    private IEventBus mEventBus;
    private ILogger mLogger;
    private IInjector mInjector;

    @Override
    public void setApplication(IApplication application) {
        mApplication = application;
    }

    @Override
    public IApplication getApplication() {
        return mApplication;
    }


    public void setInjector(IInjector injector) {
        this.mInjector = injector;
    }

    public IInjector getInjector() {
        return mInjector;
    }

    @Override
    public boolean isObjectOfType(Object t, Class<?> objectClass) {
        return objectClass.isInstance(t);
    }

    @Override
    public void setReady(boolean isReady) {
        mIsReady = isReady;
    }

    @Override
    public boolean isReady() {
        return mIsReady;
    }

    @Override
    public IEventBus getEventBus() {
        if (mEventBus == null) {
            mEventBus = new EventBus();
        }
        return mEventBus;
    }

    @Override
    public ILogger getLogger() {
        if (mLogger == null) {
            mLogger = new Logger();
        }
        return mLogger;
    }

    @Override
    public void release() {
        mApplication = null;
        if (mEventBus != null) {
            mEventBus.flushBus();
        }
        mEventBus = null;
        mLogger = null;
    }

    @Override
    public IThread currentThread() {
        return new ThreadWrapper(Thread.currentThread());
    }

}
