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

package com.jvanila.mobile.mvp;

import com.jvanila.IPlatform;
import com.jvanila.PlatformLocator;
import com.jvanila.core.eventbus.IEvent;
import com.jvanila.core.eventbus.IEventBus;
import com.jvanila.core.eventbus.IEventSubscriber;
import com.jvanila.core.log.ILogger;
import com.jvanila.mobile.core.ApplicationController;
import com.jvanila.mobile.core.IApplication;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class Presenter<V extends IPresentableView> extends Processor<V> implements
        ApplicationController.ApplicationLifeCycleObserver, IEventSubscriber {

    private static String TAG = Presenter.class.getSimpleName();
    
    protected WeakReference<V> mViewWeakReference;

    protected List<WeakReference<Presenter>> mFragmentPresenterList;
    protected Presenter<?> mParentPresenter;

    protected boolean mIsOnCreateCalled;
    protected boolean mIsOnStartCalled;
    protected boolean mIsOnResumeCalled;
    protected boolean mIsOnPauseCalled;
    protected boolean mIsOnStopCalled;//Patch Work
    protected boolean mIsDestroying;
    protected boolean mViewIsMovingOut;

    protected IPlatform mPlatform;
    protected IApplication mApplication;
    protected IEventBus mEventBus;

    protected ILogger mLogger;

    public Presenter(V view) {
        mPlatform = PlatformLocator.getPlatform();
        setView(view);
        TAG = getClass().getSimpleName();
        initInternal();
    }

    private void initInternal() {
        mEventBus = mPlatform.getEventBus();
        mLogger = mPlatform.getLogger();

        mFragmentPresenterList = new ArrayList<>();
    }

    public V getView() {
        return mViewWeakReference == null ? null : mViewWeakReference.get();
    }

    public void setView(V view) {
        mViewWeakReference = new WeakReference<>(view);
    }

    private void setParentPresenter(Presenter<?> presenter) {
        mParentPresenter = presenter;
    }

    public Presenter<?> getParentPresenter() {
        return mParentPresenter;
    }

    @SuppressWarnings("unchecked")
    public void addFragmentPresenter(Presenter presenter) {
        presenter.setParentPresenter(this);
        boolean found = false;
        for (WeakReference<Presenter> fcWeakRef : mFragmentPresenterList) {
            if (fcWeakRef.get() != null && fcWeakRef.get().equals(presenter)) {
                found = true;
                break;
            }
        }
        if (!found) {
            WeakReference<Presenter> presenterWeakRef = new WeakReference<>(presenter);
            mFragmentPresenterList.add(presenterWeakRef);
        }
    }

    public void removeFragmentPresenter(Presenter presenter) {
        if (mFragmentPresenterList != null) {
            for (WeakReference<Presenter> fcWeakRef : mFragmentPresenterList) {
                if (fcWeakRef.get() != null && fcWeakRef.get().equals(presenter)) {
                    mFragmentPresenterList.remove(fcWeakRef);
                    fcWeakRef.clear();
                    break;
                }
            }
        }
    }

    public boolean isFragmentPresenterAdded(Presenter presenter) {
        boolean found = false;
        for (WeakReference<Presenter> fcWeakRef : mFragmentPresenterList) {
            if (fcWeakRef.get() != null && fcWeakRef.get().equals(presenter)) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    @Override
    public void onApplicationReady() {
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onApplicationReady");
        }
        onReady();
    }

    @Override
    public void onApplicationLoading() {
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onApplicationLoading");
        }
        //Show loading bar
    }

    @Override
    public void onApplicationLoadingError(Throwable e) {
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onApplicationLoadingError");
        }
        //show or handle error
    }
    
    public void onCreate() {
        mIsOnCreateCalled = true;
        mIsOnStartCalled = false;
        mIsOnStopCalled = false;
        mIsOnResumeCalled = false;
        mIsOnPauseCalled = false;

        if (mLogger.canLog()) {
            mLogger.log(TAG, "onCreate");
        }

        mApplication = mPlatform.getApplication();

        if (!getView().isInstanceOf(IFragmentView.class)) {
            onCreateInternal();
        }
    }

    private void onCreateInternal() {
        if (mLogger.canLog()) {
            mLogger.log(TAG, " onCreateInternal");
        }

        /*
         * handle any activity/view controller related work here, then call onCreate on fragments/
         * subviews.
         */

        if (mFragmentPresenterList.size() > 0) {
            for (WeakReference<Presenter> fcWeakRef : mFragmentPresenterList) {
                if (fcWeakRef.get() != null) {
                    fcWeakRef.get().onCreateInternal();
                }
            }
        }

        if (mIsOnStartCalled) {
            onStart();
            if (mIsOnResumeCalled) {
                onResume();
            }
        }
    }

    public void onStart() {
        mIsOnStartCalled = true;
        mIsOnStopCalled = false;
        mIsOnResumeCalled = false;
        mIsOnPauseCalled = false;

        boolean isBroken = !mPlatform.isReady();
        if (isBroken) {
            initInternal();
            mIsOnStartCalled = false;
            onCreate();

            mApplication.getController().onFactoryBroken();
        }
        else if (!mIsOnCreateCalled) {
            onCreate();
            return;
        }

        if (mLogger.canLog()) {
            mLogger.log(TAG, " onStart");
        }

        onStartInterceptable();
        onStartSubscribe();
        mApplication.getController().onDemandCheckOnReady(this);
    }

    protected void onStartInterceptable() {
    }

    protected void onStartSubscribe() {
    }

    public void onResume() {
        mIsOnResumeCalled = true;
        mIsOnPauseCalled = false;
        if (mLogger.canLog()) {
            mLogger.log(TAG, " onResume");
        }
    }

    protected void onReady() {
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onReady");
        }
    }

    @Override
    public void onEvent(IEvent event) {
        if (mIsDestroying || mViewIsMovingOut) {
            return;
        }

        if (mLogger.canLog()) {
            mLogger.log(TAG, "onEvent "+ TAG + "received " + event.stringify());
        }
    }

    public void onPause() {
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onPause");
        }
    }

    public void onBackPressed() {
        if (mLogger == null) {
            return;
        }
        
        if (mLogger.canLog()) {
            mLogger.log(TAG, "onBackPressed");
        }
        
        for (WeakReference<Presenter> fcWeakRef : mFragmentPresenterList) {
            Presenter<?> fragmentPresenter = fcWeakRef.get();
            if (fragmentPresenter != null) {
                fragmentPresenter.onBackPressed();
            }
        }
    }
    
    public void onStop() {
        if (mIsOnStopCalled) {
            return;
        }
        mIsOnStopCalled = true;
        mIsOnStartCalled = false;
        mIsOnPauseCalled = true;
        mIsOnResumeCalled = false;

        if (mLogger.canLog()) {
            mLogger.log(TAG, "onStop");
        }

        onStopUnsubscribe();
    }

    protected void onStopUnsubscribe() {
    }

    public void onDestroy() {
        if (mIsDestroying) {
            return;
        }

        mIsDestroying = true;

        if (!mIsOnStopCalled) {
            onStop();
        }

        if (mLogger.canLog()) {
            mLogger.log(TAG, "onDestroy");
        }

        onDestroyUnsubscribe();

        mFragmentPresenterList.clear();
        mViewWeakReference.clear();

        mParentPresenter = null;
        mPlatform = null;
        mFragmentPresenterList = null;
        mViewWeakReference = null;

        mApplication = null;
        mEventBus = null;

        mLogger = null;
        TAG = null;
    }

    protected void onDestroyUnsubscribe() {
    }
}
