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

package com.jvanila.mobile.mvp;

import com.jvanila.IPlatform;
import com.jvanila.PlatformLocator;
import com.jvanila.core.exception.VanilaException;

import java.lang.ref.WeakReference;

public abstract class Binder<V extends IBindableView> extends Processor<V> {

    protected WeakReference<V> mViewWeakRef;
    protected IPlatform mPlatform;

    public Binder(V view) {
        mPlatform = PlatformLocator.getPlatform();
        mViewWeakRef = new WeakReference<>(view);
    }

    public V getView() {
        return mViewWeakRef.get();
    }

    public void onCreate() {
    }

    public abstract boolean onBind(Object... models) throws VanilaException;

    public void onDestroy() {
        if (mViewWeakRef != null) {
            mViewWeakRef.clear();
            mViewWeakRef = null;
        }
        mPlatform = null;
    }
}
