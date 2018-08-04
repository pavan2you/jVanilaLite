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
package com.jvanila.mobile.data;

import com.jvanila.core.eventbus.IEvent;
import com.jvanila.core.objectflavor.IComparableObject;
import com.jvanila.mobile.mvp.IBindableView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * By default, any Value objects used in framework or framework based apps need to make their
 * Data transfer objects as DataObjects.
 *
 * By default they are publishable.
 *
 */
public abstract class DataObject implements IEvent {

    public static final String CLASS_NAME = DataObject.class.getName();

    private static final long serialVersionUID = 1L;

    public transient WeakReference<?> tag;
    public transient List<WeakReference<?>> tagList;

    public DataObject() {
        tagList = new ArrayList<>();
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

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String stringify() {
        return toString();
    }

    public void refreshBindedViews() {
        List<WeakReference<?>> viewWeakReferences = tagList;
        if (viewWeakReferences != null) {
            int startSize = viewWeakReferences.size();
            for (int  i = 0; i < viewWeakReferences.size(); i++) {
                WeakReference<?> viewWeakReference = viewWeakReferences.get(i);
                Object taggedView = viewWeakReference.get();
                if (taggedView != null) {
                    IBindableView itemView = (IBindableView) taggedView;
                    itemView.refresh();
                }

                int newSize = viewWeakReferences.size();
                if (startSize > newSize) {
                    i = i - (startSize - newSize);
                }
            }
        }
    }

    public void swapTagsInBindedViews(DataObject target) {
        List<WeakReference<?>> viewWeakReferences = tagList;

        if (target.tagList == null) {
            target.tagList = new ArrayList<>();
        }

        if (viewWeakReferences != null) {

            int startSize = viewWeakReferences.size();

            for (int  i = 0; i < viewWeakReferences.size(); i++) {

                WeakReference<?> viewWeakReference = viewWeakReferences.get(i);
                Object taggedView = viewWeakReference.get();

                if (taggedView != null) {
                    IBindableView itemView = (IBindableView) taggedView;
                    itemView.addTag(target);
                }

                int newSize = viewWeakReferences.size();
                if (startSize > newSize) {
                    i = i - (startSize - newSize);
                }
            }
        }
    }

    public void traverse(IBindableViewVisitor callback) {
        List<WeakReference<?>> viewWeakReferences = tagList;

        if (viewWeakReferences != null) {
            int startSize = viewWeakReferences.size();

            for(int i = 0; i < viewWeakReferences.size(); ++i) {

                WeakReference<?> viewWeakReference = viewWeakReferences.get(i);
                Object taggedView = viewWeakReference.get();

                if (taggedView != null) {
                    IBindableView itemView = (IBindableView)taggedView;
                    callback.visit(itemView, this);
                }

                int newSize = viewWeakReferences.size();
                if (startSize > newSize) {
                    i -= startSize - newSize;
                }
            }
        }
    }
}
