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
import com.jvanila.core.eventbus.IEvent;
import com.jvanila.core.eventbus.IEventSubscriber;
import com.jvanila.core.exception.VanilaException;
import com.jvanila.core.objectflavor.VanilaObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LaunchTimeDependencyFactory extends VanilaObject implements IEventSubscriber {

    private SoftReference<ICallback> mCallback;
    private AtomicBoolean mLoading;

    public interface ICallback {

        void onLaunchTimeDependenciesLoading();

        void onLaunchTimeDependenciesLoaded();

        void onLaunchTimeDependenciesLoadingError(Throwable e);
    }

    protected IPlatform mPlatform;
    protected List<LaunchTimeDependency> mDependencyList;

    public LaunchTimeDependencyFactory() {
        mPlatform = PlatformLocator.getPlatform();
    }

    public void init() {
        mDependencyList = new ArrayList<>();
    }

    public void load(ICallback callback) {
        mCallback = new SoftReference<>(callback);
        callback.onLaunchTimeDependenciesLoading();
        loadAsync();
    }

    /**
     * loadAsync implementation must add each <code>LaunchTimeDependency</code>
     * to <code>mDependencyList</code>, then it should trigger <code>loadInternal</code>
     */
    protected abstract void loadAsync();

    /**
     * Call this from loadAsync implementation.
     */
    protected void loadInternal() {
        try {
            if (mPlatform.currentThread().isMain()) {
                throw new VanilaException("CalledOnMainThreadException");
            }

            mLoading = new AtomicBoolean(true);
            subscribe();

            for (LaunchTimeDependency dependency : mDependencyList) {
                if (dependency != null && dependency.needLoading()) {
                    dependency.load();
                }
            }
        }
        catch (Exception e) {
            onLoadingFailure(e);
        }
    }

    protected void subscribe() {
        mPlatform.getEventBus().subscribe(LaunchTimeDependencyEvent.CLASS_NAME, this);
    }

    protected void onLoaded() {
        cancelOngoingDependencies();
        unsubscribe();
        if (mCallback != null) {
            mCallback.get().onLaunchTimeDependenciesLoaded();
        }
        mLoading.set(false);
    }

    protected void onLoadingFailure(Throwable error) {
        cancelOngoingDependencies();
        unsubscribe();
        if (mCallback != null) {
            mCallback.get().onLaunchTimeDependenciesLoadingError(error);
        }
        mLoading.set(false);
    }

    protected void unsubscribe() {
        mPlatform.getEventBus().unsubscribe(LaunchTimeDependencyEvent.CLASS_NAME,
                this);
    }

    private void cancelOngoingDependencies() {
        for (LaunchTimeDependency dependency : mDependencyList) {
            if (dependency == null) {
                continue;
            }
            if (!dependency.isLoaded() || !dependency.isFailed()) {
                dependency.cancel();
            }
        }
    }

    @Override
    public void onEvent(IEvent event) {
        if (event.isInstanceOf(LaunchTimeDependencyEvent.class)) {
            checkIsLoadingCompleted();
        }
    }

    private void checkIsLoadingCompleted() {
        if (mDependencyList.size() == 0) {
            return;
        }

        boolean loaded = true;
        Throwable error = null;

        for (LaunchTimeDependency dependency : mDependencyList) {
            if (dependency == null) {
                continue;
            }

            loaded = dependency.isLoaded();
            if (!loaded) {
                error = dependency.getError();
                break;
            }
        }

        if (loaded) {
            onLoaded();
        }
        else if (error != null) {
            onLoadingFailure(error);
        }
    }

    public void release() {
        cancelOngoingDependencies();
        unsubscribe();
        mLoading = null;
        mCallback.clear();
        mCallback = null;
    }
}
