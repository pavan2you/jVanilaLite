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

package com.jvanila.droid.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

public class DeviceUtils {

	public static final class DeviceIdentifier {

		public static String androidDeviceId;
		public static String telephonyDeviceId;
		public static String wifiMacAddress;
	}

	public static final class DeviceDisplayMetrics {

		public static String resolution;
		public static float densityDpi;
		public static float densityDpiBucket;
		public static int widthInPx;
		public static int heightInPx;
		public static float widthInDpi;
		public static float heightInDpi;
	}

	public static String getDeviceModel() {
		return Build.MODEL;
	}

	public static String getDeviceManufacturer() {
		return Build.MANUFACTURER;
	}

	public static String getDeviceMakeModel() {
		String deviceManufacturer = Build.MANUFACTURER;
		String deviceModel = Build.MODEL;

		String make_model = new StringBuilder().append(deviceManufacturer).append("_").append(
				deviceModel).toString();
		return make_model;
	}

	public static String getOsName() {
		return "Android";
	}

	public static String getOsVersion() {
		return Build.VERSION.RELEASE;
	}
	
	public static void loadDeviceInfo(Context context) {
		DeviceUtils.loadDeviceDisplayMetrics(context);
		DeviceUtils.loadDeviceUniqueIds(context);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public static void loadDeviceDisplayMetrics(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width, height;
		Point p = new Point();
		display.getSize(p);
		width = p.x;
		height = p.y;
		
		String resolution = width + "x" + height;
		float density = context.getResources().getDisplayMetrics().density;
		float densityBucket = width / density; // if w=1200 and d = 2; then b = 1200/2 = 600sw 

		DeviceDisplayMetrics.widthInPx = width;
		DeviceDisplayMetrics.heightInPx = height;
		DeviceDisplayMetrics.widthInDpi =
				context.getResources().getDisplayMetrics().widthPixels / density;
		DeviceDisplayMetrics.heightInDpi =
				context.getResources().getDisplayMetrics().heightPixels / density;
		DeviceDisplayMetrics.resolution = resolution;
		DeviceDisplayMetrics.densityDpi = density;
		DeviceDisplayMetrics.densityDpiBucket = densityBucket;
	}

	public static void loadDeviceUniqueIds(Context context) {
		if (context instanceof Activity) {
			//loadTelephonyDeviceId(context); todo : not required for now
		}

		if (context.getPackageManager().hasSystemFeature("android.hardware.wifi")) {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            DeviceIdentifier.wifiMacAddress = wInfo.getMacAddress();
		}
		
		DeviceIdentifier.androidDeviceId = Settings.Secure.getString(context.getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}
}
