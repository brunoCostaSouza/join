package br.com.bruno.join.view

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import br.com.bruno.join.R
import br.com.bruno.join.util.ChartValueFormat
import br.com.bruno.join.databinding.ActivityChartBindingImpl
import br.com.bruno.join.enums.TypeTransaction
import br.com.bruno.join.viewModel.ChartViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import formatMoney
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_chart.bar_graph
import kotlinx.android.synthetic.main.activity_chart.pie_graph

class ChartActivity : AppCompatActivity() {

    private lateinit var viewModel: ChartViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, ChartViewModel.Factory(this))
            .get(ChartViewModel::class.java)
        val binding: ActivityChartBindingImpl =
            DataBindingUtil.setContentView(this, R.layout.activity_chart)
        binding.viewModel = viewModel

        setupView()
        setupViewModel()
        viewModel.starCharts()

    }

    private fun setupView() {
        bar_graph.apply {
            setFitBars(true)
            animateY(1000)
            setDrawGridBackground(false)
            description = Description().apply { text = "" }
        }

        pie_graph.apply {
            animateXY(2000, 2000)
            setNoDataText("")
            setUsePercentValues(false)
            elevation = 1f
        }
    }

    private fun setupViewModel() {
        compositeDisposable.add(viewModel.totalReceitaDespesa.subscribe {
            val barEntries = mutableListOf<BarEntry>()

            barEntries.add(BarEntry(1f, (it[TypeTransaction.RECEITA.name] ?: error("")).toFloat()))
            barEntries.add(BarEntry(2f, (it[TypeTransaction.DESPESA.name] ?: error("")).toFloat()))

            val barDataSet = BarDataSet(barEntries, "Receita | Despesa")
            val colors: IntArray =
                intArrayOf(ColorTemplate.rgb("#05b197"), ColorTemplate.rgb("#efb6b9"))
            barDataSet.colors = colors.toList()
            barDataSet.valueFormatter = ChartValueFormat()

            val barData = BarData(barDataSet)
            barData.barWidth = 0.9f
            barData.setValueTextSize(13f)

            bar_graph.data = barData
            bar_graph.isDoubleTapToZoomEnabled = false
            bar_graph.xAxis.isEnabled = false
            bar_graph.axisRight.isEnabled = false
            bar_graph.axisLeft.axisMaximum =
                (it[TypeTransaction.RECEITA.name] ?: error("")).toFloat() + 50
            bar_graph.axisLeft.axisMinimum = 0f
            bar_graph.invalidate()
        })

        compositeDisposable.add(viewModel.totalPorCategoria.subscribe {
            val pieEntries = mutableListOf<PieEntry>()
            for (item in it.entries) {
                val pieEntry = PieEntry(item.value.toFloat(), item.value.formatMoney())
                pieEntry.label = item.key.descricao
                pieEntries.add(pieEntry)
            }

            val dataSet = PieDataSet(pieEntries, " ")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
            dataSet.valueFormatter = ChartValueFormat()
            dataSet.valueTextSize = 15f
            dataSet.sliceSpace = 2f
            dataSet.isHighlightEnabled = true

            val pieData = PieData(dataSet)
            pie_graph.data = pieData
            pie_graph.description = Description().apply { text = " " }
            pie_graph.isDrawHoleEnabled = true
            pie_graph.setCenterTextSize(15f)
            pie_graph.center.x = 20f
            pie_graph.isHovered = false
            pie_graph.invalidate()
        })
    }
}
