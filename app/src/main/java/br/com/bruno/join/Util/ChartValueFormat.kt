package br.com.bruno.join.Util

import br.com.bruno.join.extensions.formatMoney
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

class ChartValueFormat: IValueFormatter {

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        return  entry!!.y.formatMoney()
    }
}