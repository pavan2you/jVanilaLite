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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class LaunchTimeDependencyFactory extends VanilaObject implements IEventSubscriber {

    private WeakReference<LaunchTimeDependencyFactory.ICallback> mCallback;

    public interface ICallback {

        void onLaunchTimeDependenciesLoading();

        void onLaunchTimeDependenciesLoaded();

        void onLaunchTimeDependenciesLoadingError(Exception e);
    }

    protected List<LaunchTimeDependency> mDependencyList;

    public void init() {
        mDependencyList = new ArrayList<>();
    }

    public void load(ICallback callback) {
        mCallback = new WeakReference<>(callback);
        callback.onLaunchTimeDependenciesLoading();
        loadAsync();
    }

    protected abstract void loadAsync();

    /**
     * Call this from loadAsync implementation.
     */
    protected void loadInternal() {
        try {

            IPlatform platform = PlatformLocator.getPlatform();
            if (platform.currentThread().isMain()) {
                throw new VanilaException("CalledOnMainThreadException");
            }

            for (LaunchTimeDependency dependency : mDependencyList) {
                if (dependency != null) {
                    if (!dependency.needLoading()) {
                        dependency.load();
                    }
                }
            }
        }
        catch (Exception e) {
            if (mCallback != null) {
                mCallback.get().onLaunchTimeDependenciesLoadingError(e);
            }
        }
    }

    protected void onLoaded() {
        unsubscribe();
        if (mCallback != null) {
            mCallback.get().onLaunchTimeDependenciesLoaded();
        }
    }

    protected void unsubscribe() {
        for (LaunchTimeDependency dependency : mDependencyList) {
            if (dependency != null) {
                dependency.cancel();
                dependency.unsubscribe();
            }
        }
    }

    @Override
    public void onEvent(IEvent event) {
        /*
         * Process LaunchTimeDependency loading responses
         */

        checkIsLoadingCompleted();
    }

    private void checkIsLoadingCompleted() {
        if (mDependencyList.size() > 0) {
            boolean loaded = true;
            for (LaunchTimeDependency dependency : mDependencyList) {
                if (dependency != null) {
                    loaded = dependency.isLoaded();
                    if (!loaded) {
                        break;
                    }
                }
            }
            if (loaded) {
                onLoaded();
            }
        }
    }

    public void release() {
        unsubscribe();
        mCallback = null;
    }
}
