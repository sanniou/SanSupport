/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sanniou.support.lifecycle

/**
 * allow call getValue in some times after call setValue,or will return null
 */
open class OnceLiveData<T>(private var visitableCount: Int = 0) : SingleLiveData<T>() {

    private var visitedCount = 0

    override fun postValue(t: T?) {
        visitedCount = 0
        super.postValue(t)
    }

    override fun setValue(t: T?) {
        visitedCount = 0
        super.setValue(t)
    }

    override fun getValue() =
        if (visitableCount - visitedCount++ > 0) {
            super.getValue()
        } else {
            null
        }
}