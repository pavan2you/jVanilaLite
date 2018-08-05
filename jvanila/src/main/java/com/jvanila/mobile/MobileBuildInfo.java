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

import com.jvanila.core.BuildInfo;


@SuppressWarnings("serial")
public class MobileBuildInfo extends BuildInfo {

    public static final String CLASS_NAME = MobileBuildInfo.class.getName();

	public String serverBaseUrl;
	public String serverApiBaseUrl;
	public String serverMqttBaseUrl;
	public String appPublishedStoreUrl;

	@Override
	public String stringify() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("MobileBuildInfo [appVersion=").append(appVersion)
				.append(", vcsRevisionNumber=").append(vcsRevisionNumber)
				.append(", previousAppDataVersion=").append(previousAppDataVersion)
				.append(", currentAppDataVersion=").append(currentAppDataVersion)
				.append(", serverBaseUrl=").append(serverBaseUrl)
				.append(", serverApiBaseUrl=").append(serverApiBaseUrl)
				.append(", serverMqttBaseUrl=").append(serverMqttBaseUrl)
				.append(", appPublishedStoreUrl=").append(appPublishedStoreUrl)
                .append("]");

		return buffer.toString();
	}

}
