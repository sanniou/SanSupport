package com.sanniou.support.databinding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.blankj.utilcode.util.KeyboardUtils

@BindingAdapter("selection")
fun setSelection(editText: EditText, length: Int) {
    if (editText.text.length >= length) {
        editText.setSelection(length)
    }
}

@BindingAdapter("showKeyboard")
fun showKeyboard(view: EditText, focused: Boolean) {
    if (focused) {
        KeyboardUtils.showSoftInput(view)
    } else {
        KeyboardUtils.hideSoftInput(view)
        view.clearFocus()
    }
}
