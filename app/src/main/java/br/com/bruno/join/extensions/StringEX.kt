package br.com.bruno.join.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.unFormatMoney(): Double{
    val str = this
        .replace("R$", "")
        .replace(".", "")
        .replace(",", ".")
        .replace(" ", "")
        .trim()
    return str.toDouble()
}

fun String.unFormatData(): Date {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return format.parse(this)
}

fun String.replaceAllCharacters(): String {
    return replace(".", "")
        .replace("R$", "")
        .replace(",", ".")
        .replace(" ", "")
}