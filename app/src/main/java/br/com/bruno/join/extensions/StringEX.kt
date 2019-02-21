package br.com.bruno.join.extensions

fun String.unFormatMoney(): Double{
    this.replace("R$", "")
    this.replace(".", "")
    this.replace(",", ".")
    return this.toDouble()
}