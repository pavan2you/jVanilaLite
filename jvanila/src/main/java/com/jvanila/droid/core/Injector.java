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

import android.util.Log;

import com.jvanila.PlatformLocator;
import com.jvanila.core.di.IInjector;
import com.jvanila.core.objectflavor.VanilaObject;
import com.jvanila.droid.util.DeviceUtils;
import com.jvanila.mobile.MobilePlatformInfo;

/**
 * Created by pavan on 04/08/18
 *
 */
public abstract class Injector extends VanilaObject implements IInjector {

    protected VanilaApplication mApplication;

    public Injector(VanilaApplication application) {
        mApplication = application;
    }

    @Override
    public void injectOnAppInit() {
        preparePlatformInfo();
        prepareBuildInfo();
    }

    @Override
    public void injectOnAppReady() {
    }

    protected void preparePlatformInfo() {
        Log.i(getClass().getSimpleName(), "preparePlatformInfo..");
        MobilePlatformInfo mobilePlatformInfo = PlatformLocator.getPlatform().getPlatformInfo();
        mobilePlatformInfo.osName = DeviceUtils.getOsName();
        mobilePlatformInfo.osVersion = DeviceUtils.getOsVersion();
        mobilePlatformInfo.deviceIdentifier = DeviceUtils.DeviceIdentifier.androidDeviceId;
        mobilePlatformInfo.deviceManufacturer = DeviceUtils.getDeviceManufacturer();
        mobilePlatformInfo.deviceModel = DeviceUtils.getDeviceModel();
        mobilePlatformInfo.deviceResolution = DeviceUtils.DeviceDisplayMetrics.resolution;
        mobilePlatformInfo.deviceResolutionDpi = DeviceUtils.DeviceDisplayMetrics.densityDpi;
        mobilePlatformInfo.deviceResolutionDpiBucket =
                DeviceUtils.DeviceDisplayMetrics.densityDpiBucket;
        mobilePlatformInfo.deviceWidthInPx = DeviceUtils.DeviceDisplayMetrics.widthInPx;
        mobilePlatformInfo.deviceHeightInPx = DeviceUtils.DeviceDisplayMetrics.heightInPx;
        mobilePlatformInfo.deviceWidthInDpi = DeviceUtils.DeviceDisplayMetrics.widthInDpi;
        mobilePlatformInfo.deviceHeightInDpi = DeviceUtils.DeviceDisplayMetrics.heightInDpi;
    }

    protected abstract void prepareBuildInfo();

    public void release() {
        mApplication = null;
    }
}
