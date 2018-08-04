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

package com.jvanila.droid.log;

import android.util.Log;

import com.jvanila.core.log.ILogger;
import com.jvanila.core.objectflavor.VanilaObject;

public class Logger extends VanilaObject implements ILogger {

    private boolean mQuiteModeOn;

    @Override
    public void log(String tag, String text) {
        if (mQuiteModeOn) {
            return;
        }
        Log.d(tag, text);
    }

    @Override
    public boolean canLog() {
        return !mQuiteModeOn;
    }

    @Override
    public void setKeepQuiet(boolean quiet) {
        mQuiteModeOn = quiet;
    }
}
