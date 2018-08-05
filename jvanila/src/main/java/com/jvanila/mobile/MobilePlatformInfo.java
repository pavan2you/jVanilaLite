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
package com.jvanila.mobile;

import com.jvanila.core.PlatformInfo;


@SuppressWarnings("serial")
public class MobilePlatformInfo extends PlatformInfo {

    public static final String CLASS_NAME = MobilePlatformInfo.class.getName();

	public String deviceResolution;
	public float deviceResolutionDpi;
	public float deviceResolutionDpiBucket;
	public String deviceIdentifier;
	public int deviceWidthInPx;
	public int deviceHeightInPx;
	public float deviceWidthInDpi;
	public float deviceHeightInDpi;

	public String getDeviceMakeModel() {
		StringBuilder buffer = new StringBuilder();
		String make_model = buffer.append(deviceManufacturer).append("_")
				.append(deviceModel).toString();		
		return make_model;
	}

	@Override
	public String stringify() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("MobilePlatformInfo [osName=").append(osName)
				.append(", osVersion=").append(osVersion)
				.append(", deviceModel=").append(deviceModel)
				.append(", deviceManufacturer=").append(deviceManufacturer)
				.append(", deviceWidthInPx=").append(deviceWidthInPx)
				.append(", deviceHeightInPx=").append(deviceHeightInPx)
				.append(", deviceWidthInDpi=").append(deviceWidthInDpi)
				.append(", deviceHeightInDpi=").append(deviceHeightInDpi)
				.append(", deviceResolution=").append(deviceResolution)
				.append(", deviceResolutionDpi=").append(deviceResolutionDpi)
				.append(", deviceResolutionDpiBucket=")
				.append(deviceResolutionDpiBucket);
		
		return buffer.toString();
	}

}
