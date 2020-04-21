package com.sanniou.support.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabsIntent

private const val ACTION_CUSTOM_TABS_CONNECTION =
    "android.support.customtabs.action.CustomTabsService"


fun openUrl(context: Context, url: String, @ColorInt color: Int = Color.WHITE): Boolean {
    // Here is a method that returns the chrome package name
    val uri = Uri.parse(url)
    val packageName: String =
        getCustomTabsPackages(context, uri).firstOrNull()
            ?.activityInfo?.packageName
            ?: getNativeApp(context, uri)
                .firstOrNull()
                ?.activityInfo?.packageName
            ?: return false

    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setToolbarColor(color)
        .build()

    customTabsIntent.intent.setPackage(packageName)
    customTabsIntent.launchUrl(context, uri)

    return true

}


fun getNativeApp(
    context: Context,
    uri: Uri
): List<ResolveInfo> {
    val pm = context.packageManager

    //Get all Apps that resolve a random url
    val browserActivityIntent =
        Intent(Intent.ACTION_VIEW, uri)
    val resolvedBrowserList =
        pm.queryIntentActivities(browserActivityIntent, 0)
    val specializedActivityIntent = Intent(Intent.ACTION_VIEW, uri)
    val resolvedSpecializedList =
        pm.queryIntentActivities(specializedActivityIntent, 0)
    resolvedSpecializedList.removeAll(resolvedBrowserList)
    return resolvedBrowserList
}


fun getCustomTabsPackages(context: Context, uri: Uri): ArrayList<ResolveInfo> {
    val pm: PackageManager = context.packageManager
    // Get default VIEW intent handler.
    val activityIntent = Intent(Intent.ACTION_VIEW, uri)

    // Get all apps that can handle VIEW intents.
    val resolvedActivityList =
        pm.queryIntentActivities(activityIntent, 0)
    val packagesSupportingCustomTabs: ArrayList<ResolveInfo> = ArrayList()
    for (info in resolvedActivityList) {
        val serviceIntent = Intent()
        serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
        serviceIntent.setPackage(info.activityInfo.packageName)
        if (pm.resolveService(serviceIntent, 0) != null) {
            packagesSupportingCustomTabs.add(info)
        }
    }
    return packagesSupportingCustomTabs
}