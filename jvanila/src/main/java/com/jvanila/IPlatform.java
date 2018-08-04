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

package com.jvanila;

import com.jvanila.core.IThread;
import com.jvanila.core.eventbus.IEventBus;
import com.jvanila.core.log.ILogger;
import com.jvanila.mobile.core.IApplication;

public interface IPlatform {

    /**
     * Inject application instance, if the presentation logic needs access to this, it can access
     * it in a very simplistic way.
     */
    void setApplication(IApplication application);

    IApplication getApplication();

    /**
     * To check object type
     */
    boolean isObjectOfType(Object t, Class<?> objectClass);

    /**
     * Platform health check utility flag
     */
    void setReady(boolean isReady);

    boolean isReady();

    IEventBus getEventBus();

    ILogger getLogger();

    IThread currentThread();

    void release();
}
