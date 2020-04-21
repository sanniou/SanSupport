package com.sanniou.support.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.BoolRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.Utils;

public class ResourcesUtils {


    public static Drawable getDrawable(@DrawableRes int res) {
        return ContextCompat.getDrawable(Utils.getApp(), res);
    }

    public static int getColor(@ColorRes int res) {
        return ContextCompat.getColor(Utils.getApp(), res);
    }

    public static ColorStateList getColorStateList(@ColorRes int res) {
        return ContextCompat.getColorStateList(Utils.getApp(), res);
    }

    public static String getString(@StringRes int res) {
        return Utils.getApp().getResources().getString(res);
    }

    public static String getString(@StringRes int res, Object... objects) {
        return Utils.getApp().getResources().getString(res, objects);
    }

    public static float getDimension(@DimenRes int res) {
        return Utils.getApp().getResources().getDimension(res);
    }

    public static int getDimensionPixelSize(@DimenRes int res) {
        return Utils.getApp().getResources().getDimensionPixelSize(res);
    }

    public static int getDimensionPixelOffset(@DimenRes int res) {
        return Utils.getApp().getResources().getDimensionPixelOffset(res);
    }

    public static boolean getBoolean(@BoolRes int res) {
        return Utils.getApp().getResources().getBoolean(res);
    }

    public static String[] getStringArray(@ArrayRes int res) {
        return Utils.getApp().getResources().getStringArray(res);
    }

    public static int[] getIntArray(@ArrayRes int res) {
        return Utils.getApp().getResources().getIntArray(res);
    }

    public static int getInteger(@IntegerRes int res) {
        return Utils.getApp().getResources().getInteger(res);
    }
}
