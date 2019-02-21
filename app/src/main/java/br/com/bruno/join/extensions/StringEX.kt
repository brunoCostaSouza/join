package br.com.bruno.join.extensions

fun String.unFormatMoney(): Double{
    var str = this.replace("R$", "").replace(".", "").replace(",", ".")
    return str.toDouble()
}