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
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jvanila.BuildConfig;
import com.jvanila.PlatformLocator;
import com.jvanila.core.BuildInfo;
import com.jvanila.droid.Platform;
import com.jvanila.mobile.MobileBuildInfo;
import com.jvanila.mobile.core.ApplicationController;
import com.jvanila.mobile.core.IApplication;
import com.jvanila.mobile.core.LaunchTimeDependencyFactory;

public class VanilaApplication extends Application implements IApplication {

	protected Platform mPlatform;

	private ApplicationController<VanilaApplication> mController;
	private boolean mIsApplicationRunningInForeground;
	private boolean mIsInInitialization;
	private boolean mIsCorrupted;
	protected int mRelaunchAfterInterval;

	@Override
	public void onCreate() {
		super.onCreate();
        Log.i(getClass().getSimpleName(), "onCreate..");
		mRelaunchAfterInterval = 1000;
		loadPlatformFactory();
		onCreateController();
	}

	private void loadPlatformFactory() {
		mPlatform = newPlatform();
		mPlatform.setApplication(this);
		PlatformLocator.setPlatform(mPlatform);
	}

	@NonNull
	protected Platform newPlatform() {
		return new Platform();
	}

	protected void onCreateController() {
		mController = new ApplicationController<>(this);
		mController.loadWith(newLibraryFactory());
		mController.onCreate();
	}

	@NonNull
	protected LaunchTimeDependencyFactory newLibraryFactory() {
		return new DefaultLaunchDependencyFactory();
	}

	@Override
    public ApplicationController<?> getController() {
        return mController;
    }

	@Override
	public synchronized boolean init() {
        Log.i(getClass().getSimpleName(), "init..");

        mPlatform.getLogger().setKeepQuiet(!BuildConfig.DEBUG);

        mPlatform.setInjector(newInjector());
		mPlatform.getInjector().injectOnAppInit();

		return true;
	}

	@NonNull
	protected Injector newInjector() {
		return new DefaultInjector(this);
	}

    public boolean isInInitialization() {
        return mIsInInitialization;
    }

    public synchronized void setIsInInitialization(boolean init) {
        mIsInInitialization = init;
    }

    public void setApplicationRunningInForeground(boolean b) {
		this.mIsApplicationRunningInForeground = b;
	}
	
	public boolean isApplicationRunningInForeground() {
		return mIsApplicationRunningInForeground;
	}

	boolean isCorrupted() {
		return mIsCorrupted;
	}

	@Override
	public String getClassName() {
		return getClass().getName();
	}

	@Override
	public String stringify() {
		return toString();
	}


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  EXIT APPLICATION  ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void exitApplication() {
		Log.i(getClass().getSimpleName(), "exitApplication..");
        mController.onDestroy();
		releaseResources(true);
		forceKillProcess();
	}
	
	public void releaseResources(boolean nullifyFactory) {
		Log.i(getClass().getSimpleName(), "releaseResources.." + nullifyFactory);

        mIsInInitialization = false;
        mPlatform.release();
        if (nullifyFactory) {
            mPlatform = null;
            PlatformLocator.setPlatform(null);
        }
        else {
            mPlatform.setApplication(this);
        }
	}
	
	public void forceKillProcess() {
		Process.killProcess(Process.myPid());
	}
	
	@SuppressLint("ApplySharedPref")
	@Override
	public void relaunch(boolean fromApps) {
		Log.i(getClass().getSimpleName(), "relaunch..");

		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				"vanila_preferences", 0); // 0 - for private mode
		SharedPreferences.Editor editor = pref.edit();

		int kvCount = pref.getInt("relaunch_count", 0);
		if (!fromApps && kvCount >= 1) {
			editor.remove("relaunch_count").commit();
		}
		else if (!fromApps && !mIsCorrupted) {
			mIsCorrupted = true;
		}
		else {
			editor.putInt("relaunch_count", kvCount + 1).commit();

			AppRelaunchHandler.relauchAfter(this, mRelaunchAfterInterval);
			exitApplication();
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	protected class DefaultLaunchDependencyFactory extends LaunchTimeDependencyFactory {

		@Override
		protected void loadAsync() {
			onLoaded();
		}
	}

	protected static class DefaultInjector extends Injector {

		public DefaultInjector(VanilaApplication application) {
			super(application);
		}

		@Override
		protected void prepareBuildInfo() {
			MobileBuildInfo buildInfo = PlatformLocator.getPlatform().getBuildInfo();
			buildInfo.buildType = BuildConfig.DEBUG ?
					BuildInfo.BUILD_TYPE_DEBUG : BuildInfo.BUILD_TYPE_RELEASE;
			buildInfo.currentBuildId = String.valueOf(BuildConfig.VERSION_CODE);
			buildInfo.appVersion = BuildConfig.VERSION_NAME;
		}
	}
}
