package com.sanniou.support.utils

import android.content.pm.ApplicationInfo
import com.blankj.utilcode.util.Utils

fun isDebug() =
    (Utils.getApp().getApplicationInfo() != null
        && Utils.getApp().getApplicationInfo().flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
