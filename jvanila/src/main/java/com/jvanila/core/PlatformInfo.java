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
package com.jvanila.core;


import com.jvanila.core.objectflavor.VanilaObject;

@SuppressWarnings("serial")
public class PlatformInfo extends VanilaObject {

    public static final String CLASS_NAME = PlatformInfo.class.getName();

    public String osName;
	public String osVersion;
	public String deviceModel;
	public String deviceManufacturer;

    public PlatformInfo() {
    }

    @Override
	public String stringify() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("PlatformInfo [osName=").append(osName)
				.append(", osVersion=").append(osVersion)
				.append(", deviceModel=").append(deviceModel)
				.append(", deviceManufacturer=").append(deviceManufacturer);

		return buffer.toString();
	}

}
