package br.com.bruno.join.bindable

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import formatMoney
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import java.text.NumberFormat
import java.util.*
import java.util.Collections.replaceAll



@BindingAdapter("formatMoney")
fun TextView.setFormatMoney(value: String?) {
    if (value != null) {
        this.text = value?.toDouble()?.formatMoney()
    } else {
        this.text = 0.0.formatMoney()
    }
}