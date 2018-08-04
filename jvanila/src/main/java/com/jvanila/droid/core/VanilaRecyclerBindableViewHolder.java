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

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jvanila.core.exception.VanilaException;
import com.jvanila.core.objectflavor.IComparableObject;
import com.jvanila.mobile.data.DataObject;
import com.jvanila.mobile.mvp.Binder;
import com.jvanila.mobile.mvp.IBindableView;
import com.jvanila.mobile.mvp.NullBinder;
import com.jvanila.mobile.mvp.Processor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by pavan on 12/02/16.
 *
 */
public class VanilaRecyclerBindableViewHolder<T> extends RecyclerView.ViewHolder
        implements IBindableView<T> {

    public static final int TAG_ID_4_BINDER = 999;

    protected final View mView;
    protected T mTag;
    protected Binder<?> mBinder;
    protected String mBinderClass;
    protected Object[] mAdditionalBindingArgs;
    protected boolean mDoNotRemoveSameClassTags;

    public VanilaRecyclerBindableViewHolder(View binderTaggedView) {
        super(binderTaggedView);
        mView = binderTaggedView;
        mBinderClass = (String) binderTaggedView.getTag(TAG_ID_4_BINDER);
        onCreate();
    }

    public VanilaRecyclerBindableViewHolder(View view, String binderClass) {
        super(view);
        mView = view;
        mBinderClass = binderClass;
        onCreate();
    }

    protected void onCreate() {
        mDoNotRemoveSameClassTags = false;
        loadBinder();
    }

    protected void loadBinder() {
        try {
            String binderClass = mBinderClass;
            if (binderClass != null) {
                if (binderClass.startsWith(".")) {
                    binderClass = mView.getContext().getPackageName() + binderClass;
                }
                onCreateBinder(binderClass);
            }
            else {
                mBinder = new NullBinder(this);
            }
            mBinder.onCreate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onCreateBinder(String binderClassName) {
        Class<Binder<IBindableView<T>>> binderClass;
        try {
            binderClass = (Class<Binder<IBindableView<T>>>) Class.forName(binderClassName);
            Constructor[] constructors = binderClass.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                Class<?>[] pTypes  = constructor.getParameterTypes();
                if (pTypes.length > 1) {
                    continue;
                }
                mBinder = binderClass.cast(constructor.newInstance(this));
                if (mBinder != null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View getView() {
        return mView;
    }

    /**
     * Call this whenever wants to bind some data to view
     *
     * @param dataModel the binded model
     * @param additionalBindingArgs additional arguments if any
     */
    public void onBind(T dataModel, Object... additionalBindingArgs) {
        mAdditionalBindingArgs = additionalBindingArgs;
        Object[] args = null;
        if (additionalBindingArgs != null) {
            if (dataModel != null) {
                Object[] temp = new Object[additionalBindingArgs.length + 1];
                temp[0] = dataModel;
                System.arraycopy(additionalBindingArgs, 0, temp, 1,
                        additionalBindingArgs.length);
                args = temp;
            }
            else if (additionalBindingArgs.length > 0) {
                args = additionalBindingArgs;
            }
        }
        else if (dataModel != null) {
            args = new Object[] {dataModel};
        }

        try {
            mBinder.onBind(args);
        }
        catch (VanilaException e) {
            e.printStackTrace();
        }
    }

    public void onRebind() {
        onBind(mTag, mAdditionalBindingArgs);
    }

    public void onDestroy() {
        if (mTag != null && mTag instanceof DataObject) {

            DataObject taggedDataObject = ((DataObject) mTag);
            if (taggedDataObject.tag != null) {
                taggedDataObject.tag.clear();
            }

            List<WeakReference<?>> weakRefs = taggedDataObject.tagList;
            if (weakRefs != null) {
                for (WeakReference<?> weakRef : weakRefs) {
                    if (weakRef != null && weakRef.get() == this) {
                        weakRefs.remove(weakRef);
                        weakRef.clear();
                        break;
                    }
                }
            }
        }
        mView.setTag(null);
    }

    @Override
    public void showView() {
        mView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideView() {
        mView.setVisibility(View.GONE);
    }

    @Override
    public void addTag(T tag) {
        mTag = tag;
        mView.setTag(mTag);
        if (tag != null && tag instanceof DataObject) {
            DataObject dataObject = ((DataObject) tag);
            List<WeakReference<?>> tags = dataObject.tagList;

            boolean found = false;
            if (tags != null) {

                int startSize = tags.size();

                for (int i = 0; i < tags.size(); i++) {

                    WeakReference<?> _tag = tags.get(i);
                    Object view = _tag.get();

                    if (view == this) {
                        found = true;
                        break;
                    }
                    else if (!mDoNotRemoveSameClassTags && view != null &&
                            view.getClass().getName().equals(this.getClass().getName())) {
                        tags.remove(_tag);
                        break;
                    }

                    int newSize = tags.size();
                    if (startSize > newSize) {
                        i = i - (startSize - newSize);
                    }
                }

                if (!found) {
                    WeakReference<IBindableView<T>> taggedView =
                            new WeakReference<IBindableView<T>>(this);
                    tags.add(taggedView);
                }
            }
            else {
                System.out.println("BindingDebug : failed to tag " + tag + " for " + this);
            }
        }
    }

    public void setDoNotRemoveSameClassTags(boolean doNotRemoveSameClassTags) {
        this.mDoNotRemoveSameClassTags = doNotRemoveSameClassTags;
    }

    @Override
    public T getTag() {
        return mTag;
    }

    @Override
    public Binder<? extends IBindableView> getBinder() {
        return mBinder;
    }

    @Override
    public void refresh() {
        onRebind();
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String stringify() {
        return toString();
    }

    @Override
    public Processor<?> getProcessor() {
        return mBinder;
    }

    @Override
    public boolean isEqualsTo(IComparableObject object) {
        return equals(object);
    }

    @Override
    public int hashOfObject() {
        return hashCode();
    }

    @Override
    public boolean isInstanceOf(Class<?> clazz) {
        return clazz != null && clazz.isAssignableFrom(getClass());
    }
}
