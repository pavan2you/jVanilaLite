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

import com.jvanila.core.objectflavor.IVanilaObject;

/**
 * An abstract application instance. There are scenarios to maintain global state or to inject
 * only once or to load platform specific stuff or to find out app life cycle related things, to
 * deal any such stuff this interface lets to do the same.
 *
 */
public interface IApplication extends IVanilaObject {

    /**
     * application state onInit
     */
    boolean init();

    boolean isInInitialization();

    void setIsInInitialization(boolean init);

    /**
     * application state foreground or background
     */
    boolean isApplicationRunningInForeground();

    /**
     * application state relaunch
     */
    void relaunch(boolean fromApps);

    /**
     * Application's Controller
     */
	ApplicationController<?> getController();
}
