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
import com.jvanila.core.log.ILogger;
import com.jvanila.core.objectflavor.VanilaObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController<A extends IApplication> extends VanilaObject {

    public interface ApplicationLifeCycleObserver {

        void onApplicationReady();

        void onApplicationLoading();

        void onApplicationLoadingError(Exception e);
    }

    public static final int STATE_ON_INIT       = 0;
    public static final int STATE_ON_CREATE     = 1;
    public static final int STATE_ON_LOADING    = 2;
    public static final int STATE_ON_READY      = 3;
    public static final int STATE_ON_DESTROYING = 4;

    public static final String TAG = "ApplicationController";

    protected WeakReference<A> mAppWeakRef;

    private IPlatform mPlatform;
    private ILogger mLogger;
    private LibraryFactory mLibraryFactory;

    private int mApplicationState;

    protected List<WeakReference<ApplicationLifeCycleObserver>> mLifeCycleObservers;

    public ApplicationController(A app) {
        mPlatform = PlatformLocator.getPlatform();
        mAppWeakRef = new WeakReference<>(app);
        initInternal();
    }

    public void loadWith(LibraryFactory libraryFactory) {
        mLibraryFactory = libraryFactory;
    }

    private void initInternal() {
        mApplicationState = STATE_ON_INIT;
        mLifeCycleObservers = new ArrayList<>();
        mLogger = mPlatform.getLogger();
    }

    public A getApplication() {
        return mAppWeakRef.get();
    }

    public void onCreate() {
        mApplicationState = STATE_ON_CREATE;

        if (mLogger.canLog()) {
            mLogger.log(TAG, "onCreate");
        }

        mLibraryFactory.init();
        loadLibraries();
    }

    public void onFactoryBroken() {
        boolean isBroken = !mPlatform.isReady();

        if (isBroken) {
            mPlatform.setReady(true);
            initInternal();
            onCreate();
        }
    }

    private void loadLibraries() {
        mApplicationState = STATE_ON_LOADING;

        mLibraryFactory.load(new LibraryFactory.ICallback() {

            @Override
            public void onLibrariesLoading() {
                onLoading();
            }

            @Override
            public void onLibrariesLoaded() {
                onReady();
            }

            @Override
            public void onLibrariesLoadingError(Exception e) {
                onFailed(e);
            }
        });
    }

    protected void onFailed(Exception e) {
        for (WeakReference<ApplicationLifeCycleObserver> observerWeakRef : mLifeCycleObservers) {
            if (observerWeakRef == null) {
                continue;
            }

            ApplicationLifeCycleObserver observer = observerWeakRef.get();
            if (observer != null) {
                observer.onApplicationLoadingError(e);
            }
        }
    }

    protected void onLoading() {
        for (WeakReference<ApplicationLifeCycleObserver> observerWeakRef : mLifeCycleObservers) {
            if (observerWeakRef == null) {
                continue;
            }

            ApplicationLifeCycleObserver observer = observerWeakRef.get();
            if (observer != null) {
                observer.onApplicationLoading();
            }
        }
    }

    protected void onReady() {
        mApplicationState = STATE_ON_READY;

        for (WeakReference<ApplicationLifeCycleObserver> observerWeakRef : mLifeCycleObservers) {
            if (observerWeakRef == null) {
                continue;
            }

            ApplicationLifeCycleObserver observer = observerWeakRef.get();
            if (observer != null) {
                observer.onApplicationReady();
            }
        }
        mLifeCycleObservers.clear();
    }

    private void addOnReadyObserver(ApplicationLifeCycleObserver observer) {
        boolean found = false;

        if (mLifeCycleObservers.size() > 0) {
            for (WeakReference<ApplicationLifeCycleObserver> observerWeakRef : mLifeCycleObservers) {
                if (observerWeakRef == null) {
                    continue;
                }

                ApplicationLifeCycleObserver _observer = observerWeakRef.get();
                if (_observer != null && _observer.equals(observer)) {
                    found = true; // Observer presents, so no need to proceed
                }
            }
        }

        if (!found) {
            mLifeCycleObservers.add(new WeakReference<>(observer));
        }
    }

    public void onDemandCheckOnReady(ApplicationLifeCycleObserver observer) {
        mLogger.log(TAG, "onDemandCheckOnReady");

        addOnReadyObserver(observer);

        if (mApplicationState == STATE_ON_READY) {
            onReady();
        }
        else {
            loadLibraries();
        }
    }

    public void onDestroy() {
        mApplicationState = STATE_ON_DESTROYING;

        if (mLifeCycleObservers != null) {
            mLifeCycleObservers.clear();
            mLifeCycleObservers = null;
        }

        mLogger = null;

        if (mLibraryFactory != null) {
            mLibraryFactory.release();
            mLibraryFactory = null;
        }

        mPlatform.release();
        mPlatform = null;
    }
}
