package com.sanniou.support.databinding

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.sanniou.support.extensions.getColor
import com.sanniou.support.utils.dp2px

/**
 * ----------------------------------------------------  ----------------------------------------------------
 */
@BindingAdapter("bindHeightDp")
fun bindViewHeightDp(view: View, height: Float) {
    bindViewHeight(view, dp2px(height))
}

@BindingAdapter("bindHeight")
fun bindViewHeight(view: View, height: Int) {
    val params = view.layoutParams
    if (params != null) {
        if (params.height == height) {
            return
        }
        params.height = height
        view.requestLayout()
    }
}

@BindingAdapter("bindWidthDp")
fun bindViewWidthDp(view: View, width: Float) {
    bindViewWidth(view, dp2px(width))
}

@BindingAdapter("bindWidth")
fun bindViewWidth(view: View, width: Int) {
    val params = view.layoutParams
    if (params != null) {
        if (params.width == width) {
            return
        }
        params.width = width
        view.requestLayout()
    }
}

@BindingAdapter("android:layout_marginEnd")
fun bindViewMarginEnd(view: View, marginEnd: Float) {
    val params = view.layoutParams ?: return
    if (params is MarginLayoutParams) {
        params.marginEnd = marginEnd.toInt()
        params.rightMargin = marginEnd.toInt()
        view.requestLayout()
    }
}

@BindingAdapter("android:layout_margin")
fun bindViewMargin(view: View, margin: Int) {
    view.post(Runnable {
        val params = view.layoutParams ?: return@Runnable
        if (params is MarginLayoutParams) {
            params.setMargins(margin, margin, margin, margin)
            view.requestLayout()
        }
    })
}

@BindingAdapter("focusChange")
fun bindListener(view: View, listener: OnFocusChangeListener?) {
    view.onFocusChangeListener = listener
}

@BindingAdapter("activated")
fun setViewActive(view: View, activated: Boolean) {
    view.isActivated = activated
}

@BindingAdapter("enabled")
fun setViewEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}

@BindingAdapter("selected")
fun setViewSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}

@BindingAdapter("visible")
fun setViewVisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("invisible")
fun setViewInVisible(view: View, invisible: Boolean) {
    view.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("paddingHorizontal")
fun setViewpaddingHorizontal(view: View, padding: Int) {
    view.setPadding(padding, view.paddingTop, padding, view.paddingBottom)
}

@BindingAdapter("paddingVertical")
fun setViewpaddingVertical(view: View, padding: Int) {
    view.setPadding(view.paddingStart, padding, view.paddingEnd, padding)
}

@BindingAdapter("paddingHorizontalDp")
fun setViewpaddingHorizontalDp(view: View, padding: Float) {
    view.setPadding(
        dp2px(padding), view.paddingTop, dp2px(padding),
        view.paddingBottom
    )
}

@BindingAdapter("paddingVerticalDp")
fun setViewpaddingVerticalDp(view: View, padding: Float) {
    view.setPadding(
        view.paddingStart, dp2px(padding), view.paddingEnd,
        dp2px(padding)
    )
}

@BindingAdapter(
    value = ["selectedDrawable", "normalDrawable", "pressedDrawable"],
    requireAll = false
)
fun setSelectorDrawable(
    view: View,
    selectedDrawable: Drawable?,
    normalDrawable: Drawable?,
    pressedDrawable: Drawable?
) {
    val listDrawable =
        StateListDrawable()
    if (selectedDrawable != null) {
        listDrawable.addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
    }
    if (pressedDrawable != null) {
        listDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    }
    if (normalDrawable != null) {
        listDrawable.addState(IntArray(0), normalDrawable)
    }
    val padding = intArrayOf(
        view.paddingLeft, view.paddingTop,
        view.paddingRight, view.paddingBottom
    )
    view.background = listDrawable
    view.setPadding(padding[0], padding[1], padding[2], padding[3])
}

@BindingAdapter(value = ["selectedColor", "normalColor", "pressedColor"], requireAll = false)
fun setSelectorColor(
    view: TextView,
    selected: Int,
    normal: Int,
    pressed: Int
) {
    var selectedColor = selected
    var normalColor = normal
    var pressedColor = pressed
    if (selectedColor == 0) {
        selectedColor = normalColor
    }
    if (normalColor == 0) {
        normalColor = selectedColor
    }
    if (pressedColor == 0) {
        pressedColor = normalColor
    }
    val states = arrayOf(
        intArrayOf(android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_pressed)
    )
    val colors = intArrayOf(
        selectedColor,
        normalColor,
        pressedColor
    )
    val color = ColorStateList(states, colors)
    view.setTextColor(color)
}

@BindingAdapter(
    value = ["backgroundColor", "backgroundColorRes", "backgroundColorStr", "backgroundRadius", "backgroundRadiusLT", "backgroundRadiusLB", "backgroundRadiusRT", "backgroundRadiusRB", "backgroundStroke", "backgroundStrokeWidth", "backgroundStrokeWidthDP", "isRadiusAdjustBounds", "isRadiusPx"],
    requireAll = false
)
fun setViewBackground(
    view: View,
    @ColorInt colorValue: Int,
    @ColorRes colorRes: Int,
    colorStr: String,
    radiusValue: Int,
    radiusLTValue: Int,
    radiusLBValue: Int,
    radiusRTValue: Int,
    radiusRBValue: Int,
    strokeColor: Int,
    strokeWidthValue: Int,
    strokeWidthDp: Float,
    isRadiusAdjustBounds: Boolean,
    isRadiusPx: Boolean
) {
    var color = colorValue
    var radius = radiusValue
    var radiusLT = radiusLTValue
    var radiusLB = radiusLBValue
    var radiusRT = radiusRTValue
    var radiusRB = radiusRBValue
    var strokeWidth = strokeWidthValue
    if (color == 0 && colorRes == 0 && colorStr.isEmpty() && strokeWidth == 0 && strokeWidthDp == 0F
    ) {
        return
    }
    if (colorStr.isNotEmpty()) {
        color = Color.parseColor(colorStr)
    } else if (colorRes != 0) {
        color = view.getColor(colorRes)
    }
    if (strokeWidthDp != 0F) {
        strokeWidth = dp2px(strokeWidthDp)
    }
    if (isRadiusAdjustBounds) {
        radius = 0
        radiusLT = 0
        radiusRT = 0
        radiusRB = 0
        radiusLB = 0
    }
    if (!isRadiusPx) {
        if (radius != 0) {
            radius = dp2px(radius.toFloat())
        }
        if (radiusLT != 0) {
            radiusLT = dp2px(radiusLT.toFloat())
        }
        if (radiusLB != 0) {
            radiusLB = dp2px(radiusLB.toFloat())
        }
        if (radiusRT != 0) {
            radiusRT = dp2px(radiusRT.toFloat())
        }
        if (radiusRB != 0) {
            radiusRB = dp2px(radiusRB.toFloat())
        }
    }
    val drawable: GradientDrawable = AdjustGradientDrawable(isRadiusAdjustBounds)
    drawable.setColor(color)
    if (!isRadiusAdjustBounds) {
        if (radiusLB == 0 && radiusLT == 0 && radiusRB == 0 && radiusRT == 0) {
            drawable.cornerRadius = radius.toFloat()
        } else {
            val radii = floatArrayOf(
                radiusLT.toFloat(), radiusLT.toFloat(),
                radiusRT.toFloat(), radiusRT.toFloat(),
                radiusRB.toFloat(), radiusRB.toFloat(),
                radiusLB.toFloat(), radiusLB
                    .toFloat()
            )
            drawable.cornerRadii = radii
        }
    }
    if (strokeWidth != 0 && strokeColor != 0) {
        drawable.setStroke(strokeWidth, strokeColor)
    }
    val padding = intArrayOf(
        view.paddingLeft, view.paddingTop,
        view.paddingRight, view.paddingBottom
    )
    view.background = drawable
    view.setPadding(padding[0], padding[1], padding[2], padding[3])
}

/**
 * ----------------------------------------------------  ----------------------------------------------------
 */
private class AdjustGradientDrawable(private val mRadiusAdjustBounds: Boolean) :
    GradientDrawable() {
    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        if (mRadiusAdjustBounds) { // 修改圆角为短边的一半
            cornerRadius = Math.min(right - left, bottom - top) / 2.toFloat()
        }
    }

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds)
        if (mRadiusAdjustBounds) { // 修改圆角为短边的一半
            cornerRadius = bounds.width().coerceAtMost(bounds.height()) / 2.toFloat()
        }
    }

    override fun onBoundsChange(r: Rect) {
        super.onBoundsChange(r)
        if (mRadiusAdjustBounds) { // 修改圆角为短边的一半
            cornerRadius = r.width().coerceAtMost(r.height()) / 2.toFloat()
        }
    }
}