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

import com.jvanila.core.eventbus.IEvent;
import com.jvanila.core.eventbus.IEventSubscriber;

import java.lang.ref.WeakReference;

public abstract class LaunchTimeDependencyFactory implements IEventSubscriber {

    private WeakReference<LaunchTimeDependencyFactory.ICallback> mCallback;

    public interface ICallback {

        void onLaunchTimeDependenciesLoading();

        void onLaunchTimeDependenciesLoaded();

        void onLaunchTimeDependenciesLoadingError(Exception e);
    }

    public void init() {
    }

    public void load(ICallback callback) {
        mCallback = new WeakReference<>(callback);
        callback.onLaunchTimeDependenciesLoading();
        subscribe();
        loadAsync();
    }

    protected void subscribe() {
        /*
         * Subscribe LaunchTimeDependency related events
         */
    }

    protected abstract void loadAsync();

    protected void onLoaded() {
        unsubscribe();
        if (mCallback != null) {
            mCallback.get().onLaunchTimeDependenciesLoaded();
        }
    }

    protected void unsubscribe() {
        /*
         * Unsubscribe LaunchTimeDependency related events
         */
    }

    @Override
    public void onEvent(IEvent event) {
        /*
         * Process LaunchTimeDependency loading responses
         */
    }

    public void release() {
        mCallback = null;
    }
}
