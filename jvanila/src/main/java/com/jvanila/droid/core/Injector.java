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

import com.jvanila.core.di.IInjector;
import com.jvanila.core.objectflavor.VanilaObject;

/**
 * Created by pavan on 04/08/18
 *
 */
public class Injector extends VanilaObject implements IInjector {

    private VanilaApplication mApplication;

    public Injector(VanilaApplication application) {
        mApplication = application;
    }

    @Override
    public void injectOnAppInit() {
    }

    @Override
    public void injectOnAppReady() {

    }
}
