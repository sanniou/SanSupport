package com.sanniou.support.extensions

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

inline fun <reified T : Activity> Activity.startActivityEx(requestCode: Int = -1) {
    startActivityForResult(Intent(this, T::class.java), requestCode)
}

inline fun <reified T : Activity> Activity.startActivityEx(
    vararg params: String,
    requestCode: Int = -1
) {
    startActivityForResult(Intent(this, T::class.java).apply {
        params.forEachIndexed { index, param ->
            index.isOdd().takeIfTrue {
                putExtra(params[index - 1], param)
            }
        }
    }, requestCode)
}

internal fun FragmentActivity.replaceFragment(containerViewId: Int, fragment: Fragment) {
    supportFragmentManager.run {
        beginTransaction()
            .run {
                replace(containerViewId, fragment)
                addToBackStack(fragment::javaClass.name)
                commit()
            }
    }
}

internal fun FragmentActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.run {
        beginTransaction()
            .run {
                remove(fragment)
                popBackStack()
                commit()
            }
    }
}

internal fun FragmentActivity.addFragment(containerViewId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        .run {
            add(containerViewId, fragment)
            addToBackStack(fragment::javaClass.name)
            commit()
        }
}
