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
public class BuildInfo extends VanilaObject {

    public static final String CLASS_NAME = BuildInfo.class.getName();

	public static final int BUILD_TYPE_DEBUG 	= 0;
	public static final int BUILD_TYPE_RELEASE 	= 1;

	public String appVersion;
	public String vcsRevisionNumber;
	public int previousAppDataVersion;
	public int currentAppDataVersion;
	public String currentBuildId;
	public String currentBuildName;
	public int buildType;
	
	public BuildInfo() {
		buildType = BUILD_TYPE_DEBUG;
	}

	public boolean isDebugBuild() {
		return buildType == BUILD_TYPE_DEBUG;
	}
	
	public boolean isReleaseBuild() {
		return buildType == BUILD_TYPE_RELEASE;
	}

    @Override
	public String stringify() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("BuildInfo [appVersion=").append(appVersion)
				.append(", vcsRevisionNumber=").append(vcsRevisionNumber)
				.append(", previousAppDataVersion=").append(previousAppDataVersion)
				.append(", currentAppDataVersion=").append(currentAppDataVersion)
				.append(", currentBuildId=").append(currentBuildId)
				.append(", currentBuildName=").append(currentBuildName)
                .append("]");

		return buffer.toString();
	}
}
