import java.text.NumberFormat
import java.util.*

fun Double.formatMoney(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
}