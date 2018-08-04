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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jvanila.R;
import com.jvanila.core.objectflavor.IComparableObject;
import com.jvanila.mobile.mvp.IFragmentView;
import com.jvanila.mobile.mvp.IPresentableView;
import com.jvanila.mobile.mvp.NullController;
import com.jvanila.mobile.mvp.Presenter;
import com.jvanila.mobile.mvp.Processor;

import java.lang.reflect.Constructor;

public class VanilaSupportFragment extends Fragment implements IFragmentView {

    public static final String ARG_PRESENTER = "ARG_PRESENTER";

    protected Presenter<? extends IPresentableView> mPresenter;
    protected String mPresenterName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "onCreate");
        if (getArguments() != null) {
            mPresenterName = getArguments().getString(ARG_PRESENTER);
        }
        loadController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        Log.d(getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void loadController() {
        try {
            String controllerClassName = mPresenterName;
            if (controllerClassName != null) {
                if (controllerClassName.startsWith(".")) {
                    controllerClassName = getActivity().getPackageName() + controllerClassName;
                }
                onCreateController(controllerClassName);
            }
            else {
                mPresenter = new NullController<IPresentableView>(this);
            }

            mPresenter.onCreate();

            if (getParentView().getPresenter() != null) {
                getParentView().getPresenter().addFragmentPresenter(mPresenter);
            }
            else if (getParentView() instanceof VanilaAppCompatActivity) {
                VanilaAppCompatActivity activity = (VanilaAppCompatActivity) getParentView();
                activity.addTempFragmentController(mPresenter);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void onCreateController(String controllerClassName) {
        Class<Presenter<IFragmentView>> controllerClass;
        try {
            controllerClass = (Class<Presenter<IFragmentView>>) Class.forName(controllerClassName);
            Constructor[] constructors = controllerClass.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                Class<?>[] pTypes  = constructor.getParameterTypes();
                if (pTypes.length > 1) {
                    continue;
                }
                mPresenter = controllerClass.cast(constructor.newInstance(this));
                if (mPresenter != null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(getClass().getSimpleName(), "onInflate");
        onInflateLoadController(context, attrs, savedInstanceState);
    }

    protected void onInflateRemoveSelf() {
        try {
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                fm.beginTransaction().remove(this).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onInflateLoadController(Context context, AttributeSet attrs,
            Bundle savedInstanceState) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VanilaBinders);
        mPresenterName = (String) a.getText(R.styleable.VanilaBinders_presenter);
        if (mPresenterName == null && savedInstanceState != null) {
            mPresenterName = savedInstanceState.getString(ARG_PRESENTER);
        }
        a.recycle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_PRESENTER, mPresenterName);
        super.onSaveInstanceState(outState);
    }

    @Override
    public IPresentableView getParentView() {
        return (getActivity() instanceof IPresentableView) ?
                (IPresentableView) getActivity() : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Presenter<?> getPresenter() {
        return mPresenter;
    }

    @Override
    public void onStart() {
        Log.d(getClass().getSimpleName(), "onStart");
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onResume() {
        Log.d(getClass().getSimpleName(), "onResume");
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        Log.d(getClass().getSimpleName(), "onPause");
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(getClass().getSimpleName(), "onStop");
        mPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.d(getClass().getSimpleName(), "onDetach");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.d(getClass().getSimpleName(), "onDestroyView");
        removeController();
        super.onDestroyView();
    }

    private void removeController() {
        if (getParentView() != null && getParentView().getPresenter() != null) {
            getParentView().getPresenter().removeFragmentPresenter(mPresenter);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(getClass().getSimpleName(), "onDestroy");
        if (mPresenter != null) {
            removeController();
            mPresenter.onDestroy();
        }
        super.onDestroy();
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

    @Override
    public Processor<?> getProcessor() {
        return mPresenter;
    }
}
