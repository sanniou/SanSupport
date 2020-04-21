package com.sanniou.support;

import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

class Weak<T : Any>(initializer: T? = null) {

    private var reference = WeakReference(initializer)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return reference.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        reference = WeakReference(value)
    }
}
