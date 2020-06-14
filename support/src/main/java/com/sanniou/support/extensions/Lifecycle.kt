package com.sanniou.support.extensions

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableByte
import androidx.databinding.ObservableChar
import androidx.databinding.ObservableDouble
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun <T> MutableLiveData<T>.init(t: T) = this.apply { value = t }

fun <T : ViewModel> ViewModelStoreOwner.getViewModel(clazz: Class<T>) =
    ViewModelProvider(this).get(clazz)

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    factory: ViewModelProvider.Factory? = null
) = factory?.let {
    ViewModelProvider(this, it).get(T::class.java)
} ?: run {
    ViewModelProvider(this).get(T::class.java)
}

/**
 * without null check
 */
inline fun <reified T : ViewModel> Fragment.getActivityViewModel() =
    ViewModelProvider(activity!!).get(T::class.java)

fun <T : ViewModel> Fragment.getActivityViewModel(clazz: Class<T>) =
    ViewModelProvider(activity!!).get(clazz)

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y): LiveData<Y> =
    Transformations.map(this, mapFunction)

fun <X, Y> LiveData<X>.switchMap(mapFunction: (X) -> LiveData<Y>): LiveData<Y> =
    Transformations.switchMap(this, mapFunction)

fun <Y> ObservableBoolean.map(mapFunction: (Boolean) -> Y) =
    MapObservableField(observableBoolean = this, mapFunction = mapFunction)

fun <Y> ObservableByte.map(mapFunction: (Byte) -> Y) =
    MapObservableField(observableByte = this, mapFunction = mapFunction)

fun <Y> ObservableChar.map(mapFunction: (Char) -> Y) =
    MapObservableField(observableChar = this, mapFunction = mapFunction)

fun <Y> ObservableDouble.map(mapFunction: (Double) -> Y) =
    MapObservableField(observableDouble = this, mapFunction = mapFunction)

fun <X, Y> ObservableField<X>.map(mapFunction: (X) -> Y) =
    MapObservableField(observableField = this, mapFunction = mapFunction)

fun <Y> ObservableFloat.map(mapFunction: (Float) -> Y) =
    MapObservableField(observableFloat = this, mapFunction = mapFunction)

fun <Y> ObservableInt.map(mapFunction: (Int) -> Y) =
    MapObservableField(observableInt = this, mapFunction = mapFunction)

fun <Y> ObservableLong.map(mapFunction: (Long) -> Y) =
    MapObservableField(observableLong = this, mapFunction = mapFunction)

class MapObservableField<X, Y>(
    // private val observableArrayList: ObservableArrayList<X>? = null,
    // private val observableArrayMap: ObservableArrayMap<X>? = null,
    private val observableBoolean: ObservableBoolean? = null,
    private val observableByte: ObservableByte? = null,
    private val observableChar: ObservableChar? = null,
    private val observableDouble: ObservableDouble? = null,
    private val observableField: ObservableField<X>? = null,
    private val observableFloat: ObservableFloat? = null,
    private val observableInt: ObservableInt? = null,
    private val observableLong: ObservableLong? = null,
    // private val observableParcelable: ObservableParcelable<X>?=null,
    private val mapFunction: (X) -> Y
) : ObservableField<Y>(
    observableBoolean
        .whenNull(observableByte)
        .whenNull(observableChar)
        .whenNull(observableDouble)
        .whenNull(observableField)
        .whenNull(observableFloat)
        .whenNull(observableInt)
        .whenNull(observableLong) as Observable
) {

    override fun get() = mapFunction(
        (observableBoolean?.get()
            ?: observableByte?.get()
            ?: observableChar?.get()
            ?: observableDouble?.get()
            ?: observableField?.get()
            ?: observableFloat?.get()
            ?: observableInt?.get()
            ?: observableLong?.get()
            ) as X
    )
}

