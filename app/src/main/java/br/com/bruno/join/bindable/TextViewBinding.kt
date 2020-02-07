package br.com.bruno.join.bindable

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import formatMoney
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import br.com.bruno.join.R
import faranjit.currency.edittext.CurrencyEditText
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import java.util.Collections.replaceAll



@BindingAdapter("formatMoney")
fun TextView.setFormatMoney(value: String?) {
    if (value != null) {
        this.text = value.toDouble().formatMoney()
    } else {
        this.text = 0.0.formatMoney()
    }
}

@BindingAdapter("formatMoneyForeseen")
fun TextView.setFormatMoneyForeseen(value: String?) {
    if (value != null) {
        this.text = resources.getString(R.string.previsto, value.toDouble().formatMoney())
    } else {
        this.text = resources.getString(R.string.previsto, 0.0.formatMoney())
    }
}