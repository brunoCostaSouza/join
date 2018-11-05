package br.com.bruno.join.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.formataData(): String {
    val df = SimpleDateFormat("dd/MM/yyyy")
    return df.format(this)
}