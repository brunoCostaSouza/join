package br.com.bruno.join.extensions

import java.text.NumberFormat
import java.util.Locale

fun Float.formatMoney(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
}