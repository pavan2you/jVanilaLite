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
package com.jvanila.core.objectflavor;

import com.jvanila.core.exception.VanilaException;

/**
 * Maps domain object cloning methods to native equivalents.
 */
public abstract class CloneableObject extends ComparableObject implements ICloneableObject,
        Cloneable {

	@Override
	public ICloneableObject clone() {
        try {
            return deepClone();
        }
        catch (VanilaException e) {
            e.printStackTrace();
        }
        return null;
    }
}
