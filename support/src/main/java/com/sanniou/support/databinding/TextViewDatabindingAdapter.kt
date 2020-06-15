package com.sanniou.support.databinding

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.sanniou.support.extensions.getColor
import com.sanniou.support.helper.EditTextHelper
import com.sanniou.support.widget.FakeBoldSpan

@BindingAdapter("fakeBoldText")
fun bindText(view: TextView, text: CharSequence) {
    if (text.isEmpty()) {
        return
    }
    val builder = SpannableStringBuilder(text)
    builder.setSpan(FakeBoldSpan(), 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    view.text = builder
}

@BindingAdapter("imeOptions")
fun bindEditImeOption(view: TextView, imeOptions: Int) {
    view.imeOptions = imeOptions
}

@BindingAdapter("textColorRes")
fun setTextColorRes(view: TextView, color: Int) {
    view.setTextColor(view.getColor(color))
}

@BindingAdapter("android:inputType")
fun bindEditInput(view: TextView, inputType: Int) {
    view.inputType = inputType
}

@BindingAdapter("linkMovementMethod")
fun bindTextLink(view: TextView, nonuse: Boolean) {
    view.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("banSpace")
fun bindingEditInput(view: TextView?, ban: Boolean) {
    if (ban) {
        EditTextHelper.setEditTextNoSpace(view)
    }
}