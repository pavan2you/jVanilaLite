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
package com.jvanila.droid.core;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.jvanila.PlatformLocator;
import com.jvanila.core.objectflavor.IComparableObject;
import com.jvanila.droid.Platform;
import com.jvanila.mobile.mvp.IPresentableView;
import com.jvanila.mobile.mvp.NullController;
import com.jvanila.mobile.mvp.Presenter;
import com.jvanila.mobile.mvp.Processor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class VanilaAppCompatActivity extends AppCompatActivity implements IPresentableView {
	
    protected VanilaApplication mApplication;

    protected Presenter<? extends IPresentableView> mPresenter;
    private ArrayList<Presenter<? extends IPresentableView>> mTempFragmentPresenterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateModifyWindowFlags();

        mApplication = (VanilaApplication) getApplication();

        Log.d(getClass().getSimpleName(), "launching vanila activity : " + getClassName());
        loadController(savedInstanceState);

        if (mApplication.isCorrupted()) {
            finish();
            mApplication.relaunch(false);
        }
        else {
            mApplication.setCurrentActivity(this);
            onCreateView(savedInstanceState);
        }
    }

    protected void onCreateModifyWindowFlags() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    protected void onCreateView(Bundle savedInstanceState) {

    }

    protected void loadController(Bundle savedInstanceState) {
    	try {

            @SuppressLint("WrongConstant") ActivityInfo ai = getPackageManager().getActivityInfo(
                    this.getComponentName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);

            Bundle bundle = ai.metaData;
            if (bundle != null) {
                String presenter = bundle.getString("presenter");
                if (presenter != null) {
                    onCreatePresenter(presenter, savedInstanceState);
                }
            }

            if (mPresenter == null) {
                mPresenter = new NullController<IPresentableView>(this);
            }

            Platform mpf = (Platform) PlatformLocator.getPlatform();
			mpf.setApplication(mApplication);

            if (mTempFragmentPresenterList != null && mTempFragmentPresenterList.size() > 0) {
                for (Presenter p : mTempFragmentPresenterList) {
                    mPresenter.addFragmentPresenter(p);
                }
                mTempFragmentPresenterList.clear();
            }

            mPresenter.onCreate();
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onCreatePresenter(String presenterClassName, Bundle savedInstanceState) {
        Class<Presenter<IPresentableView>> presenterClass;
        try {
            if (presenterClassName.startsWith(".")) {
                presenterClassName = getPackageName() + presenterClassName;
            }
            presenterClass = (Class<Presenter<IPresentableView>>) Class.forName(presenterClassName);
            Constructor[] constructors = presenterClass.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                Class<?>[] pTypes  = constructor.getParameterTypes();
                if (pTypes.length > 1) {
                    continue;
                }

                mPresenter = presenterClass.cast(constructor.newInstance(this));
                if (mPresenter != null) {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addTempFragmentController(Presenter<? extends IPresentableView> presenter) {
        if (mTempFragmentPresenterList == null) {
            mTempFragmentPresenterList = new ArrayList<>();
        }
        mTempFragmentPresenterList.add(presenter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        Log.d(getClass().getSimpleName(), "onStart");
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getClass().getSimpleName(), "onResume");
        mApplication.setApplicationRunningInForeground(true);
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        mApplication.setApplicationRunningInForeground(false);
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(getClass().getSimpleName(), " -> : onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getSimpleName(), "onStop");
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!(mPresenter instanceof NullController)) {
            mPresenter.onBackPressed();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Presenter<?> getPresenter() {
        return mPresenter;
    }

    @Override
    public Processor<?> getProcessor() {
        return mPresenter;
    }

    @Override
    public boolean isEqualsTo(IComparableObject object) {
        return equals(object);
    }

    @Override
    public int hashOfObject() {
        return hashCode();
    }

    @Override
    public boolean isInstanceOf(Class<?> clazz) {
        return clazz != null && clazz.isAssignableFrom(getClass());
    }

	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public String stringify() {
		return toString();
	}
}
