package com.sanniou.support.lifecycle

/**
 * will not change repeat if get a same value when call setValue or postValue
 */
class UnrepeatedLiveData<T>(value: T) : NonNullLiveData<T>(value) {
    init {
        setValue(value)
    }

    override fun setValue(value: T?) {
        if (value != getValue()) {
            super.setValue(value)
        }
    }

    override fun postValue(value: T?) {
        if (value != getValue()) {
            super.postValue(value)
        }
    }
}